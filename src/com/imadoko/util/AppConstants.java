package com.imadoko.util;

/**
 * アプリケーション定数クラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class AppConstants {
    // 共通系
    public static final String SERIVCE_MESSAGE = "service_message";
    public static final String TRANSITION_TYPE = "transitionType";
    public static final String TRIGGERED_LONGITUDE = "triggeredLongitude";
    public static final String TRIGGERED_LATITUDE = "triggeredLatitude";
    public static final String GEOFENCE_REQUEST_ID = "geofenceRequestId";
    public static final int DEBUG_LOG_MAX_SIZE = 7;

    /** Action */
    public static final String ACTION = "com.imadoko.app.MainActivity";

    /** WebSocket/REST系 */
//    public static final String WEBSOCKET_SERVER_URI = "ws://imadoko-node-server.herokuapp.com";
//    public static final String AUTH_URL = "http://imadoko-node-server.herokuapp.com/auth";
//    public static final String MASTER_GEOFENCE_URL = "http://imadoko-node-server.herokuapp.com/master/geofence";
//    public static final String GEOFENCE_DATA_URL = "http://imadoko-node-server.herokuapp.com/geofence/data";
//    public static final String GEOFENCE_LOG_URL = "http://imadoko-node-server.herokuapp.com/geofence/log";
//    public static final String GEOFENCE_STATUS_URL = "http://imadoko-node-server.herokuapp.com/geofence/status";
//    public static final String REGISTER_USERNAME_URL = "http://imadoko-node-server.herokuapp.com/register/username";
    public static final String WEBSOCKET_SERVER_URI = "ws://192.168.0.30:9224";
    public static final String AUTH_URL = "http://192.168.0.30:9224/auth";
    public static final String GEOFENCE_DATA_URL = "http://192.168.0.30:9224/geofence/data";
    public static final String GEOFENCE_LOG_URL = "http://192.168.0.30:9224/geofence/log";
    public static final String GEOFENCE_STATUS_URL = "http://192.168.0.30:9224/geofence/status";
    public static final String REGISTER_USERNAME_URL = "http://192.168.0.30:9224/register/username";
    public static final String WEBSOCKET_AUTHKEY_HEADER = "X-Imadoko-AuthKey";
    public static final int SERVICE_CLOSE_CODE = 9999;
    public static final int TIMER_INTERVAL = 15000;
    public static final int FAST_RECCONECT_MAX_NUM = 10;
    public static final int RECOONECT_FAST_INTRERVAL = 5000;
    public static final int RECONNECT_INTERVAL = 100000;
    public static final int LOITERING_DELAY = 180000; // 3分

    /** Geofenceステータス */
    public static final int GEOFENCE_NOTIFICATION_OK = 1;
    public static final int GEOFENCE_NOTIFICATION_NG = 0;
    public static final int TRANSITION_TYPE_ENTER = 1;

    /** ParameterKey */
    public static final String PARAM_AUTH_KEY = "authKey";
    public static final String PARAM_USERNAME = "userName";
    public static final String PARAM_GEOFENCE_ENTITY = "geofenceEntity";
    public static final String PARAM_TRANSITION_TYPE = "transitionType";
    public static final String PARAM_DIALOG_MESSAGE = "dialogMessage";

    /** LogID */
    public static final String TAG_APPLICATION = "Application";
    public static final String TAG_ASYNCTASK = "AsyncTask";
    public static final String TAG_HTTP = "Http";
    public static final String TAG_WEBSOCKET = "WebSocket";
    public static final String TAG_SERVICE = "Service";
    public static final String TAG_LOCATION = "Location";

    /** DialogID */
    public static final String DIALOG_ALERT = "DialogAlert";
    public static final String DIALOG_SETTINGS = "DialogSettings";
    public static final String DIALOG_AUTH_ERROR = "DialogAuthError";

    /** salt */
    public static final String SECURITY_SALT = "1xd5pEBShVey9LFz3rMVHHdrRkKbLAuNuun9fS7x";

    /** UI系 */
    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;

    /** 画像 */
    public static final String CONNECTION_OK_IMAGE = "connection1.png";
    public static final String CONNECTION_NG_IMAGE = "connection2.png";
    public static final String CONNECTION_SILENT_CLOSE_IMAGE = "connection3.png";

    /** 接続状態 */
    public enum CONNECTION {
        APPLICATION_CREATE("アプリケーション起動"),
        APPLICATION_DESTROY("アプリケーション終了"),
        APPLICATION_PAUSE("アプリケーション一時停止"),
        APPLICATION_START("アプリケーション開始"),
        APPLICATION_STOP("アプリケーション停止"),
        CONNECTED("接続開始"),
        CONNECTING("接続確立"),
        DISCONNECT("接続切断"),
        RECONNECTING("再接続中"),
        RECONNECT("再接続開始"),
        SILENT_CLOSE("無通信切断"),
        SEND_PING("ping送信"),
        RECEIVE_PONG("ping受信"),
        RESTART("サービス再起動"),
        LOCATION_OK("位置情報返却成功"),
        LOCATION_NG("位置情報取得失敗"),
        GEOFENCE_IN("周辺に進入"),
        GEOFENCE_OUT("周辺を退出"),
        GEOFENCE_STAY("に滞在開始"),
        GEOFENCE_ERROR("ジオフェンスエラー"),
        SERVICE_DEAD("想定外のサービス停止"),
        USERNAME_REGISTER_OK("ユーザ名登録成功"),
        USERNAME_REGISTER_NG("ユーザ名登録失敗");

        private String status;

        private CONNECTION(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }
}
