package com.imadoko.app;

/**
 * アプリケーション定数クラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class AppConstants {
    // 共通系
    public static final String SERIVCE_MESSAGE = "service_message";
    public static final String AUTH_OK = "a1";
    public static final String AUTH_NG = "a2";
    public static final String EXCEPTION = "e";

    /** WebSocket系 */
    public static final String WEBSOCKET_SERVER_URI = "ws://imadoko-node-server.herokuapp.com";
//    public static final String WEBSOCKET_SERVER_URI = "ws://192.168.0.30:9224";
    public static final String AUTH_URL = "http://imadoko-node-server.herokuapp.com/auth";
//    public static final String AUTH_URL = "http://192.168.0.30:9224/auth";
    public static final String WEBSOCKET_AUTHKEY_HEADER = "X-Imadoko-AuthKey";
    public static final int TIMER_INTERVAL = 15000;
    public static final int FAST_RECCONECT_MAX_NUM = 10;
    public static final int RECOONECT_FAST_INTRERVAL = 5000;
    public static final int RECONNECT_INTERVAL = 100000;
    /** LogID */
    public static final String TAG_APPLICATION = "Application";
    public static final String TAG_ASYNCTASK = "AsyncTask";
    public static final String TAG_HTTP = "Http";
    public static final String TAG_WEBSOCKET = "WebSocket";
    public static final String TAG_SERVICE = "Service";
    public static final String TAG_LOCATION = "Location";
    /** salt */
    public static final String SECURITY_SALT = "1xd5pEBShVey9LFz3rMVHHdrRkKbLAuNuun9fS7x";

    /** UI系 */
    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;
}
