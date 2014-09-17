package com.imadoko.app;

/**
 * アプリケーション定数クラス
 * @author mapserver2007
 * @since 2014/09/06
 */
public class AppConstants {
    // 共通系
    public static final String SERIVCE_MESSAGE = "service_message";
    
    public static final String AUTH_OK = "a1";
    public static final String AUTH_NG = "a2";
    
    public static final String EXCEPTION = "e";
    
    /** WebSocketサーバ URI */
    public static final String WEBSOCKET_SERVER_URI = "ws://imadoko-node-server.herokuapp.com";
//    public static final String WEBSOCKET_SERVER_URI = "ws://192.168.0.4:9224";
    public static final String AUTH_URL = "http://imadoko-node-server.herokuapp.com/auth";
//    public static final String AUTH_URL = "http://192.168.0.4:9224/auth";
    /** LogID */
    public static final String TAG_APPLICATION = "Application";
    public static final String TAG_ASYNCTASK = "AsyncTask";
    public static final String TAG_HTTP = "Http";
    public static final String TAG_WEBSOCKET = "WebSocket";
    public static final String TAG_SERVICE = "Service";
    public static final String TAG_LOCATION = "Location";
    /** salt */
    public static final String SECURITY_SALT = "1xd5pEBShVey9LFz3rMVHHdrRkKbLAuNuun9fS7x";
    
    public static final String WEBSOCKET_AUTHKEY_HEADER = "X-Imadoko-AuthKey";
    public static final long TIMER_INTERVAL = 10000;
    public static final long RECONNECT_INTERVAL = 100000;
}
