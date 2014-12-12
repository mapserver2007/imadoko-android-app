package com.imadoko.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.arnx.jsonic.JSON;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocket.READYSTATE;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.imadoko.R;
import com.imadoko.activity.MainActivity;
import com.imadoko.entity.GeofenceParcelable;
import com.imadoko.entity.WebSocketEntity;
import com.imadoko.util.AppConstants;
import com.imadoko.util.AppConstants.CONNECTION;

/**
 * ConnectionService
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class ConnectionService extends Service {
    private WebSocketClient _ws;
    private String _authKey;
    private String _locationQuality;
    private ArrayList<GeofenceParcelable> _geofenceList;
    private LocationClient _locationClient;
    private GooglePlayServicesClient.ConnectionCallbacks _connectionCallbacks;
    private GooglePlayServicesClient.OnConnectionFailedListener _onConnectionFailedListener;
    private LocationClient.OnAddGeofencesResultListener _onAddGeofencesResultListener;
    private LocationClient.OnRemoveGeofencesResultListener _onRemoveGeofencesByRequestIdsResult;
    private LocationRequest _locationRequest;
    private LocationListener _locationListener;
    private WebSocketEntity _responseEntity;
    private LinkedList<Long> _heartbeatPool;
    private Timer _heartbeatTimer;
    private int _recconectCount;
    private NotificationCompat.Builder _notify;

    @Override
    public void onCreate() {
        super.onCreate();
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
            _ws.getConnection().close(AppConstants.SERVICE_CLOSE_CODE);
            _ws = null;
        }
        if (_locationClient != null) {
            _locationClient.disconnect();
        }
        if (_heartbeatTimer != null) {
            _heartbeatTimer.cancel();
            _heartbeatTimer.purge();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        _authKey = intent.getStringExtra(AppConstants.PARAM_AUTH_KEY);
        _locationQuality = intent.getStringExtra(AppConstants.PARAM_LOCATION_QUALITY);
        _geofenceList = intent.getExtras().getParcelableArrayList(AppConstants.PARAM_GEOFENCE_ENTITY);
        createNotification();

        if (_ws == null || _ws.getReadyState() != READYSTATE.OPEN) {
            createWebSocketConnection();
        }

        return START_REDELIVER_INTENT;
    }

    /**
     * LocationClientを起動する
     */
    private void createLocationManager() {
        /**
         * 接続時・切断時のコールバック
         */
        _connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                removeLocationRequest();
                updateLocationRequest();
                addGeofence();
            }

            @Override
            public void onDisconnected() {
                removeLocationRequest();
                removeGeofence();
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

        /**
         * GeoFence解除後のコールバック
         */
        _onRemoveGeofencesByRequestIdsResult = new LocationClient.OnRemoveGeofencesResultListener() {
            @Override
            public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
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

            @Override
            public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
            }
        };

        _locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                sendBroadcast(CONNECTION.LOCATION_UPDATE);
            }
        };

        _locationRequest = LocationRequest.create();
        _locationClient = new LocationClient(this, _connectionCallbacks, _onConnectionFailedListener);
        if (_locationClient.isConnected()) {
            removeLocationRequest();
        }
        _locationClient.connect();
    }

    private void updateLocationRequest() {
        long interval = AppConstants.LOCATION_QUALITY_HIGH.equals(_locationQuality) ?
                AppConstants.LOCATION_LOW_INTERVAL : AppConstants.LOCATION_HIGH_INTERVAL;
        float displacement = AppConstants.LOCATION_QUALITY_HIGH.equals(_locationQuality) ?
                AppConstants.SMALLEST_LOW_DISPLACEMENT : AppConstants.SMALLEST_HIGH_DISPLACEMENT;

        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        _locationRequest.setInterval(interval);
        _locationRequest.setFastestInterval(interval);
        _locationRequest.setSmallestDisplacement(displacement);
        _locationClient.requestLocationUpdates(_locationRequest, _locationListener);
    }

    private void removeLocationRequest() {
        _locationClient.removeLocationUpdates(_locationListener);
    }

    private void addGeofence() {
        List<Geofence> geofences = new ArrayList<Geofence>();
        for (GeofenceParcelable entity : _geofenceList) {
            Geofence.Builder builder = new Geofence.Builder();
            builder.setRequestId(entity.getRequestId());
            builder.setCircularRegion(Double.parseDouble(entity.getLat()),
                    Double.parseDouble(entity.getLng()), Float.parseFloat(entity.getRadius()));
            builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
            builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
            builder.setLoiteringDelay(entity.getLoiteringDelay());
            geofences.add(builder.build());
        }

        Intent intent = new Intent(this, GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        _locationClient.addGeofences(geofences, pendingIntent, _onAddGeofencesResultListener);
    }

    private void removeGeofence() {
        List<String> requestIdList = new ArrayList<String>();
        for (GeofenceParcelable entity : _geofenceList) {
            requestIdList.add(entity.getRequestId());
        }
        _locationClient.removeGeofences(requestIdList, _onRemoveGeofencesByRequestIdsResult);
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
        } catch (Throwable e) {
            sendBroadcast(CONNECTION.DISCONNECT);
            return;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AppConstants.WEBSOCKET_AUTHKEY_HEADER, _authKey);
        headers.put(AppConstants.WEBSOCKET_APPLICATION_TYPE_HEADER, AppConstants.APPLICATION_TYPE);

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
                _heartbeatTimer = new Timer(true);
                _heartbeatTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (_ws == null || _ws.getReadyState() != READYSTATE.OPEN) {
                            _heartbeatTimer.cancel();
                            _heartbeatTimer.purge();
                            return;
                        }

                        if (_heartbeatPool.size() > 2) {
                            getConnection().close(AppConstants.SERVICE_CLOSE_CODE);
                            return;
                        }
                        _heartbeatPool.add(System.currentTimeMillis());
                        FramedataImpl1 frame = new FramedataImpl1(Opcode.PING);
                        frame.setFin(true);
                        _ws.getConnection().sendFrame(frame);
                        sendBroadcast(CONNECTION.SEND_PING);
                    }
                }, AppConstants.PING_TIMER_INTERVAL, AppConstants.PING_TIMER_INTERVAL);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onClose");
                _heartbeatTimer.cancel();
                _heartbeatTimer.purge();

                if (isServiceRunning(ConnectionService.class)) {
                    if (code != AppConstants.SERVICE_CLOSE_CODE) { // サーバからの切断
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
                        sendBroadcast(CONNECTION.DISCONNECT);
                    }
                } else {
                    // サービスが死んだ時
                    sendBroadcast(CONNECTION.RECONNECT);
                }
            }

            @Override
            public void onError(Exception e) {
                getConnection().close(AppConstants.SERVICE_CLOSE_CODE);
            }

            @Override
            public void onMessage(final String jsonStr) {
                _responseEntity = JSON.decode(jsonStr, WebSocketEntity.class);
                if (!_authKey.equals(_responseEntity.getAuthKey())) {
                    Log.d(AppConstants.TAG_WEBSOCKET, "auth error at getLocation");
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (_locationClient.isConnected()) {
                            Location location = _locationClient.getLastLocation();
                            if (location != null) {
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
                sendBroadcast(CONNECTION.RECEIVE_PONG);
            }
        };

        _ws.connect();
    }

    /**
     * BroadcastReceiverへステータスを送信
     * @param status 接続ステータス
     */
    private void sendBroadcast(CONNECTION status) {
        Intent intent = new Intent(AppConstants.ACTION);
        intent.putExtra(AppConstants.SERIVCE_MESSAGE, status);
        sendBroadcast(intent);
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