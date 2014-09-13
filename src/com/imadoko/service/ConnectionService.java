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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

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
    private LocationManager _locationManager;
    private LocationListener _locationListener;
    
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
        _locationListener = new LocationListener() {
            @Override public void onLocationChanged(final Location location) {}
            @Override public void onProviderDisabled(final String provider) {}
            @Override public void onProviderEnabled(final String provider) {}
            @Override public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
        };
        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        HandlerThread gpsThread = new HandlerThread("GPSThread");
        gpsThread.start();
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, _locationListener, gpsThread.getLooper());
    }
    
    private Location getLastKnownLocation() {
        // GPS、Wi-Fiの順にチェック
        String[] providers = new String[] { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER };
        for (String provider : providers) {
            if (provider != null && _locationManager.isProviderEnabled(provider)) {
                Location location = _locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    return location;
                }
            }
        }
        
        return null;
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
        _locationManager.removeUpdates(_locationListener);
        _locationListener = null;
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
                        Location location = getLastKnownLocation();
                        if (location != null) {
                            String lng = String.valueOf(location.getLongitude());
                            String lat = String.valueOf(location.getLatitude());
                            sendBroadcast(lng + "," + lat);
                            String json = "{\"authKey\":\"" + _authKey + "\",\"senderId\":\"" + _senderId + "\",\"request\":\"location\",\"lng\":\"" + lng + "\",\"lat\":\"" + lat + "\"}";
                            _ws.send(json);
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
