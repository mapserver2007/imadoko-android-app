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
    public static final String GEOFENCE_PLACE_ID = "geofencePlaceId";
    public static final String CONNECTION_USERS = "connectionUsers";
    public static final String SHARED_PREFERENCES_KEY = "myPref";
    public static final String LOCATION_QUALITY_KEY = "locationQuality";
    public static final String PREF_MAIN_LOG = "mainLog";
    public static final String PREF_GEOFENCE_LOG = "geofenceLog";
    public static final int DEBUG_LOG_MAX_SIZE = 7;
    public static final int GEOFENCE_LOG_MAX_SIZE = 3;
    public static final String APPLICATION_TYPE = "1";
    public static final String REQUEST_MAIN_TO_WATCHER = "2";
    public static final String REQUEST_CONNECTION_USERS = "4";
    public static final String LOCATION_QUALITY_LOW = "1";
    public static final String LOCATION_QUALITY_HIGH = "2";
    public static final String USER_CONNECT = "1";
    public static final String USER_DISCONNECT = "2";

    /** classpath */
    public static final String ACTION = "com.imadoko.activity.MainActivity";
    public static final String SERVICE = "com.imadoko.service.ConnectionService";

    /** WebSocket/REST系 */
    public static final String WEBSOCKET_SERVER_URI  = "ws://imadoko-node-server.herokuapp.com";
    public static final String AUTHSALT_URL          = "https://imadoko-node-server.herokuapp.com/salt";
    public static final String AUTH_URL              = "https://imadoko-node-server.herokuapp.com/auth";
    public static final String MASTER_GEOFENCE_URL   = "https://imadoko-node-server.herokuapp.com/master/geofence";
    public static final String GEOFENCE_DATA_URL     = "https://imadoko-node-server.herokuapp.com/geofence/data";
    public static final String GEOFENCE_LOG_URL      = "https://imadoko-node-server.herokuapp.com/geofence/log";
    public static final String GEOFENCE_STATUS_URL   = "https://imadoko-node-server.herokuapp.com/geofence/status";
    public static final String UPDATE_SETTING_URL    = "https://imadoko-node-server.herokuapp.com/setting/update";
//    public static final String WEBSOCKET_SERVER_URI  = "ws://192.168.0.30:9224";
//    public static final String AUTHSALT_URL          = "http://192.168.0.30:9224/salt";
//    public static final String AUTH_URL              = "http://192.168.0.30:9224/auth";
//    public static final String GEOFENCE_DATA_URL     = "http://192.168.0.30:9224/geofence/data";
//    public static final String GEOFENCE_LOG_URL      = "http://192.168.0.30:9224/geofence/log";
//    public static final String GEOFENCE_STATUS_URL   = "http://192.168.0.30:9224/geofence/status";
//    public static final String UPDATE_SETTING_URL    = "http://192.168.0.30:9224/setting/update";
    public static final String WEBSOCKET_AUTHKEY_HEADER = "X-Imadoko-AuthKey";
    public static final String WEBSOCKET_APPLICATION_TYPE_HEADER = "X-Imadoko-ApplicationType";
    public static final int SERVICE_CLOSE_CODE = 1002;
    public static final int WS_TIMER_INTERVAL = 30000;
    public static final int PING_TIMER_INTERVAL = 20000;
    public static final int FAST_RECCONECT_MAX_NUM = 10;
    public static final int RECOONECT_FAST_INTRERVAL = 5000;
    public static final int RECONNECT_INTERVAL = 100000;
    public static final int LOITERING_DELAY = 180000; // 3分
    public static final long LOCATION_LOW_INTERVAL = 300000L; // 5分
    public static final float SMALLEST_LOW_DISPLACEMENT = 300f; // 300m
    public static final long LOCATION_HIGH_INTERVAL = 30000L; // 30秒
    public static final float SMALLEST_HIGH_DISPLACEMENT = 100f; // 100m

    /** Geofenceステータス */
    public static final int GEOFENCE_NOTIFICATION_OK = 1;
    public static final int GEOFENCE_NOTIFICATION_NG = 0;
    public static final int TRANSITION_TYPE_ENTER = 0x1;
    public static final int TRANSITION_TYPE_EXIT = 0x2;
    public static final int TRANSITION_TYPE_DWELL = 0x4;
    public static final long VABRATION_TIME = 500;

    /** ParameterKey */
    public static final String PARAM_AUTH_KEY = "authKey";
    public static final String PARAM_LOCATION_QUALITY = "locationQuality";
    public static final String PARAM_CONNECTION_ID = "connectionId";
    public static final String PARAM_SALT_NAME = "name";
    public static final String PARAM_USERNAME = "userName";
    public static final String PARAM_LOCATION_PERMISSION = "locPermission";
    public static final String PARAM_GEOFENCE_ENTITY = "geofenceEntity";
    public static final String PARAM_PLACE_ID = "placeId";
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
    public static final String DIALOG_CREATE_USER = "DialogCreateUser";

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
        LOCATION_UPDATE("位置情報更新"),
        LOCATION_OK("位置情報返却成功"),
        LOCATION_NG("位置情報取得失敗"),
        GEOFENCE_IN("周辺に進入"),
        GEOFENCE_OUT("周辺を退出"),
        GEOFENCE_STAY("に滞在開始"),
        GEOFENCE_ERROR("ジオフェンスエラー"),
        SERVICE_DEAD("想定外のサービス停止"),
        SETTING_OK("設定更新成功"),
        SETTING_NG("設定更新失敗"),
        LOCATION_QUALITY_CHANGE("位置情報取得精度変更"),
        USER_CONNECT("ユーザが接続開始"),
        USER_DISCONNECT("ユーザが切断切断");

        private String status;

        private CONNECTION(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    /** Geofenceステータス */
    public enum GEOFENCE_STATUS {
        LOG_SAVED("Geofenceログ保存成功"),
        LOG_NOT_SAVED("Geofenceログ保存失敗"),
        NOTIFY_PATTERN_OK("通知可能なGeofence遷移:"),
        NOTIFY_PATTERN_NG("通知不許可なGeofence遷移:"),
        NOTIFY_SETTING_ENABLED("Geofence通知設定あり"),
        NOTIFY_SETTING_DISABLED("Geofence通知設定なし");

        private String status;

        private GEOFENCE_STATUS(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }

    /** 接続モード */
    public enum CONNECTION_QUALITY {
        LOW("(省電力接続モード)"),
        HIGH("(高精度接続モード)");

        private String status;

        private CONNECTION_QUALITY(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
    }
}
