package com.imadoko.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpStatus;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
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
import com.imadoko.model.AuthManager;

public class ConnectionService extends Service {

    public static final String ACTION = "ServiceAction";
    private WebSocketClient _ws;
    private Timer _timer;
    private String _authKey;
    private String _senderId;
    private LocationRequest _locationRequest;
    private LocationClient _locationClient;
    private GooglePlayServicesClient.ConnectionCallbacks _connectionCallbacks;
    private GooglePlayServicesClient.OnConnectionFailedListener _onConnectionFailedListener;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(AppConstants.TAG_SERVICE, "Service start");
        createLocationManager();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstants.TAG_SERVICE, "onStartCommand");
        startAuth();
        return START_STICKY;
    }
    
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
    
    private void sendBroadcast(String message) {
        Intent sendIntent = new Intent(ACTION);
        sendIntent.putExtra(AppConstants.SERIVCE_MESSAGE, message);
        sendBroadcast(sendIntent);
    }

    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            sendBroadcast(AppConstants.AUTH_OK);
            createWebSocketConnection();
        } else {
            sendBroadcast(AppConstants.AUTH_NG);
            return;
        }
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
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
    
    private void onWebSocketClose() {
        if (_timer != null) {
            _timer.cancel();
        }
        _ws = null;
        sendBroadcast("WebSocket終了");
    }

    private void createWebSocketConnection() {
        final Handler handler = new Handler();

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
                _timer = new Timer();
                _timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (_ws.getReadyState() == WebSocket.READYSTATE.OPEN) {
                            String json = "{\"authKey\":\"" + _authKey + "\",\"request\":\"polling_from_android\"}";
                            _ws.send(json);
                        } else {
                            _timer.cancel();
                        }
                    }
                }, AppConstants.TIMER_INTERVAL, AppConstants.TIMER_INTERVAL);
                
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast("WebSocket開始");
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onClose");
                Log.d(AppConstants.TAG_WEBSOCKET, reason);
                onWebSocketClose();
                
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast("WebSocket再接続");
                        createWebSocketConnection();
                    }
                }, AppConstants.RECONNECT_INTERVAL);
            }
            
            @Override
            public void onError(Exception e) {
                Log.d(AppConstants.TAG_WEBSOCKET, "onError");
                Log.d(AppConstants.TAG_WEBSOCKET, e.getMessage());
                sendBroadcast("WebSocketエラー");
                onWebSocketClose();
            }

            @Override
            public void onMessage(final String jsonStr) {
                JSONObject json;
                try {
                    json = new JSONObject(jsonStr);
                    _senderId = json.getString("senderId");
                    if (_authKey.equals(json.getString("authKey"))) {
                        Log.d(AppConstants.TAG_WEBSOCKET, "auth error at getLocation");
                    }
                } catch (JSONException e) {
                    Log.d(AppConstants.TAG_WEBSOCKET, "Invalid data");
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        // 接続されているときだけ現在地を取得
                        if (_locationClient.isConnected()) {
                            Location location = _locationClient.getLastLocation();
                            if (location != null) {
                                String lng = String.valueOf(location.getLongitude());
                                String lat = String.valueOf(location.getLatitude());
                                sendBroadcast(lng + "," + lat);
                                String json = "{\"authKey\":\"" + _authKey + "\",\"senderId\":\"" + _senderId + "\",\"request\":\"location\",\"lng\":\"" + lng + "\",\"lat\":\"" + lat + "\"}";
                                _ws.send(json);
                            }
                        }
                    }
                });
            }
        };

        _ws.connect();
    }
    
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
}
