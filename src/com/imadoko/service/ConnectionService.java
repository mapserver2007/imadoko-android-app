package com.imadoko.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.arnx.jsonic.JSON;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.imadoko.app.AppConstants;
import com.imadoko.app.AppConstants.CONNECTION;
import com.imadoko.app.MainActivity;
import com.imadoko.app.R;
import com.imadoko.entity.WebSocketResponseEntity;

/**
 * ConnectionService
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class ConnectionService extends Service {
    public static final String ACTION = "ServiceAction";
    private WebSocketClient _ws;
    private String _authKey;
    private LocationRequest _locationRequest;
    private LocationClient _locationClient;
    private GooglePlayServicesClient.ConnectionCallbacks _connectionCallbacks;
    private GooglePlayServicesClient.OnConnectionFailedListener _onConnectionFailedListener;
    private LocationClient.OnAddGeofencesResultListener _onAddGeofencesResultListener;
    private WebSocketResponseEntity _responseEntity;
    private LinkedList<Long> _heartbeatPool;
    private Timer _heartbeatTimer;
    private int _recconectCount;
    private NotificationCompat.Builder _notify;
    private NotificationManager _manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(AppConstants.TAG_SERVICE, "Service start");
        _heartbeatPool = new LinkedList<Long>();
        createLocationManager();
        createNotification();
        startForeground(1, _notify.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (_ws != null) {
            _ws.getConnection().closeConnection(AppConstants.SERVICE_CLOSE_CODE, null);
            _ws = null;
        }
        if (_locationClient != null) {
            _locationClient.disconnect();
        }
        if (_heartbeatTimer != null) {
            _heartbeatTimer.cancel();
        }
        Log.d(AppConstants.TAG_SERVICE, "Service end");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstants.TAG_SERVICE, "onStartCommand");
        _authKey = intent.getStringExtra("authKey");
        createNotification();
        createWebSocketConnection();
        return START_STICKY;
    }

    /**
     * LocationClientを起動する
     */
    private void createLocationManager() {
        _locationRequest = LocationRequest.create();
        _locationRequest.setInterval(10000);
        _locationRequest.setFastestInterval(3000);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /**
         * 接続時・切断時のコールバック
         */
        _connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(AppConstants.TAG_LOCATION, "location on connected");
                addGeofence();
            }

            @Override
            public void onDisconnected() {
                Log.d(AppConstants.TAG_LOCATION, "location on disconnected");
            }
        };

        /**
         * 接続失敗時のコールバック
         */
        _onConnectionFailedListener = new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d(AppConstants.TAG_LOCATION, "location connect failure");
            }
        };

        /**
         * GeoFence登録後のコールバック
         */
        _onAddGeofencesResultListener = new LocationClient.OnAddGeofencesResultListener() {
            @Override
            public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIdList) {
                switch (statusCode) {
                    case LocationStatusCodes.SUCCESS:
                        break;
                    case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    case LocationStatusCodes.ERROR:
                        break;
                    default:
                        break;
                }
                Log.d(AppConstants.TAG_SERVICE, String.valueOf(statusCode));
            }
        };

        _locationClient = new LocationClient(this, _connectionCallbacks, _onConnectionFailedListener);
        _locationClient.connect();
    }

    private void addGeofence() {
        double lat = 35.755094;
        double lng = 139.688843;
        float radius = 200;

        Geofence.Builder builder = new Geofence.Builder();
        builder.setRequestId("imadoko_fence");
        builder.setCircularRegion(lat, lng, radius);
        builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
        builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        builder.setLoiteringDelay(60 * 1000); //とりあえず

        List<Geofence> geofences = new ArrayList<Geofence>();
        geofences.add(builder.build());

//        Intent intent = new Intent(ACTION);
        Intent intent = new Intent(this, GeofenceService.class);
        //intent.putExtra(AppConstants.SERIVCE_MESSAGE, AppConstants.CONNECTION.GEOFENCE_ON);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        _locationClient.addGeofences(geofences, pendingIntent, _onAddGeofencesResultListener);
    }

    private void removeGeofence() {
        // TODO
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        _notify = new NotificationCompat.Builder(this)
            .setPriority(Notification.PRIORITY_HIGH)
            .setContentTitle("imadoko")
            .setContentText("タップしてアプリケーションを表示する")
            .setSmallIcon(R.drawable.ic_statusbar)
            .setContentIntent(contentIntent)
            .setOngoing(true);

        _manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notification(String message) {
        _manager.notify(1, _notify
                .setContentText(message)
                .setTicker(message)
                .build());
    }

    /**
     * WebSocket接続開始
     */
    private void createWebSocketConnection() {
        final Handler handler = new Handler();
        if (_recconectCount > AppConstants.FAST_RECCONECT_MAX_NUM) {
            _recconectCount = AppConstants.FAST_RECCONECT_MAX_NUM + 1;
        } else {
            _recconectCount++;
        }

        URI uri;
        try {
            uri = new URI(AppConstants.WEBSOCKET_SERVER_URI);
        } catch (URISyntaxException e) {
            sendBroadcast(CONNECTION.DISCONNECT);
            return;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AppConstants.WEBSOCKET_AUTHKEY_HEADER, _authKey);

        _ws = new WebSocketClient(uri, new Draft_17(), headers, 3000) {
            @Override
            public void onOpen(ServerHandshake handShake) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onOpen");
                _recconectCount = 0;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(CONNECTION.CONNECTED);
                    }
                });

                // HeartBaat処理
                _heartbeatTimer = new Timer();
                _heartbeatTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (_heartbeatPool.size() > 2) {
                            getConnection().closeConnection(AppConstants.SERVICE_CLOSE_CODE, null);
                            return;
                        }
                        _heartbeatPool.add(System.currentTimeMillis());
                        FramedataImpl1 frame = new FramedataImpl1(Opcode.PING);
                        frame.setFin(true);
                        _ws.getConnection().sendFrame(frame);
                        sendBroadcast(CONNECTION.SEND_PING, false);
                    }
                }, AppConstants.TIMER_INTERVAL, AppConstants.TIMER_INTERVAL);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onClose");
                _heartbeatTimer.cancel();

                if (isServiceRunning(ConnectionService.class)) {
                    if (code != AppConstants.SERVICE_CLOSE_CODE) {
                        _heartbeatPool = new LinkedList<Long>();
                        handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    sendBroadcast(CONNECTION.RECONNECTING);
                                    createWebSocketConnection();
                                }
                            },
                            _recconectCount > AppConstants.FAST_RECCONECT_MAX_NUM ? AppConstants.RECONNECT_INTERVAL
                                    : AppConstants.RECOONECT_FAST_INTRERVAL);
                    } else {
                        // サーバからの接続が切断された場合
                        sendBroadcast(CONNECTION.DISCONNECT);
                    }
                } else {
                    // サービスが死んだ時
                    sendBroadcast(CONNECTION.RECONNECT);
                }
            }

            @Override
            public void onError(Exception e) {
                getConnection().closeConnection(AppConstants.SERVICE_CLOSE_CODE, null);
            }

            @Override
            public void onMessage(final String jsonStr) {
                _responseEntity = JSON.decode(jsonStr, WebSocketResponseEntity.class);
                if (!_authKey.equals(_responseEntity.getAuthKey())) {
                    Log.d(AppConstants.TAG_WEBSOCKET, "auth error at getLocation");
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (_locationClient.isConnected()) {
                            Location location = _locationClient
                                    .getLastLocation();
                            if (location != null) {
                                _responseEntity.setRequestId("send_request_browser");
                                _responseEntity.setLng(String.valueOf(location.getLongitude()));
                                _responseEntity.setLat(String.valueOf(location.getLatitude()));
                                sendBroadcast(CONNECTION.LOCATION_OK);
                                send(JSON.encode(_responseEntity));
                            } else {
                                sendBroadcast(CONNECTION.LOCATION_NG);
                            }
                        }
                    }
                });
            }

            @Override
            public void onWebsocketPing(WebSocket conn, Framedata f) {
                _heartbeatPool.remove();
                sendBroadcast(CONNECTION.RECEIVE_PONG, false);
            }
        };

        _ws.connect();
    }

    /**
     * BroadcastReceiverへステータスを送信
     * @param status 接続ステータス
     */
    private void sendBroadcast(CONNECTION status) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(AppConstants.SERIVCE_MESSAGE, status);
        sendBroadcast(intent);
    }

    /**
     * BroadcastReceiverへステータスを送信
     * @param status 接続ステータス
     * @param 通知を有効にするかどうか
     */
    private void sendBroadcast(CONNECTION status, boolean enableNotification) {
        if (enableNotification) {
            notification(status.toString());
        }
        sendBroadcast(status);
    }

    /**
     * プロセスが生存しているか
     * @param cls クラスオブジェクト
     * @return 生存結果
     */
    private boolean isServiceRunning(Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : runningService) {
            if (cls.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
