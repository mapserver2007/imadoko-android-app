package com.imadoko.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpStatus;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import com.imadoko.app.AppConstants;
import com.imadoko.async.AsyncHttpTaskLoader;
import com.imadoko.entity.HttpEntity;
import com.imadoko.entity.WebSocketEntity;
import com.imadoko.entity.WebSocketResponseEntity;
import com.imadoko.model.AuthManager;

public class ConnectionService extends Service {

    public static final String ACTION = "ServiceAction";
    private WebSocketClient _ws;
    private Timer _timer;
    private String _authKey;
    private LocationRequest _locationRequest;
    private LocationClient _locationClient;
    private GooglePlayServicesClient.ConnectionCallbacks _connectionCallbacks;
    private GooglePlayServicesClient.OnConnectionFailedListener _onConnectionFailedListener;
    private WebSocketResponseEntity _responseEntity;
    private int _recconectCount;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(AppConstants.TAG_SERVICE, "Service start");
        createLocationManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_locationClient != null) {
            _locationClient.disconnect();
        }
        Log.d(AppConstants.TAG_SERVICE, "Service end");
        if (_timer != null) {
            _timer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstants.TAG_SERVICE, "onStartCommand");
        startAuth();
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
         * 接続時・切断時のコールバック.
         */
        _connectionCallbacks = new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(AppConstants.TAG_LOCATION, "location on connected");
            }
            @Override
            public void onDisconnected() {
                Log.d(AppConstants.TAG_LOCATION, "location on disconnected");
            }
        };

        /**
         * 接続失敗時のコールバック.
         */
        _onConnectionFailedListener = new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d(AppConstants.TAG_LOCATION, "location connect failure");
            }
        };

        _locationClient = new LocationClient(this, _connectionCallbacks, _onConnectionFailedListener);
        _locationClient.connect();
    }

    /**
     * 認証処理後のコールバック
     * @param statusCode ステータスコード
     */
    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            sendBroadcast(AppConstants.AUTH_OK);
            createWebSocketConnection();
        } else {
            sendBroadcast(AppConstants.AUTH_NG);
            return;
        }
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
            sendBroadcast(AppConstants.EXCEPTION);
            return;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AppConstants.WEBSOCKET_AUTHKEY_HEADER, _authKey);

        _ws = new WebSocketClient(uri, new Draft_17(), headers, 0) {
            @Override
            public void onOpen(ServerHandshake handShake) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onOpen");
                _recconectCount = 0;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast("WebSocket開始");
                    }
                });
                startPollingRequest();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onClose");
                Log.d(AppConstants.TAG_WEBSOCKET, reason);
                sendBroadcast("WebSocket終了");

                if (_timer != null) {
                    _timer.cancel();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast("WebSocket再接続");
                        createWebSocketConnection();
                    }
                }, _recconectCount > AppConstants.FAST_RECCONECT_MAX_NUM ? AppConstants.RECONNECT_INTERVAL : AppConstants.RECOONECT_FAST_INTRERVAL);
            }

            @Override
            public void onError(Exception e) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onError");
                Log.d(AppConstants.TAG_WEBSOCKET, e.getMessage());
                sendBroadcast("WebSocketエラー");
                _ws.close();
                _ws = null;
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
                            Location location = _locationClient.getLastLocation();
                            if (location != null) {
                                _responseEntity.setRequestId("send_request_browser");
                                _responseEntity.setLng(String.valueOf(location.getLongitude()));
                                _responseEntity.setLat(String.valueOf(location.getLatitude()));
                                sendBroadcast("位置情報取得成功");
                                String json = JSON.encode(_responseEntity);
                                _ws.send(json);
                            }
                        }
                    }
                });
            }
        };

        _ws.connect();
    }

    /**
     * リクエストポーリングを開始する
     */
    private void startPollingRequest() {
        final WebSocketEntity pollingEntity = new WebSocketEntity();
        pollingEntity.setAuthKey(_authKey);
        pollingEntity.setRequestId("polling_from_android");

        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (_ws.getReadyState() == WebSocket.READYSTATE.OPEN) {
                        _ws.send(JSON.encode(pollingEntity));
                    } else {
                        _timer.cancel();
                        _timer = null;
                    }
                } catch (WebsocketNotConnectedException e) {
                    Log.d(AppConstants.TAG_WEBSOCKET, e.getMessage());
                    _timer.cancel();
                    _timer = null;
                }
            }
        }, AppConstants.TIMER_INTERVAL, AppConstants.TIMER_INTERVAL);
    }

    /**
     * 認証処理を実行
     */
    private void startAuth() {
        String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AuthManager manager = new AuthManager(udid, AppConstants.SECURITY_SALT);
        _authKey = manager.generateAuthKey();

        HttpEntity entity = new HttpEntity();
        entity.setUrl(AppConstants.AUTH_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put("authKey", _authKey);
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(Integer statusCode) {
                onAuthResult(statusCode);
            }
        };
        loader.forceLoad();
    }

    /**
     * Activityへメッセージ送信
     * @param message
     */
    private void sendBroadcast(String message) {
        Intent sendIntent = new Intent(ACTION);
        sendIntent.putExtra(AppConstants.SERIVCE_MESSAGE, message);
        sendBroadcast(sendIntent);
    }
}
