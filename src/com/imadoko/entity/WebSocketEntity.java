package com.imadoko.entity;

/**
 * WebSocket用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class WebSocketEntity {
    /** 認証キー */
    private String _authKey;
    /** リクエストID */
    private String _requestId;
    /** コネクションID */
    private String _connectionId;
    /** 経度 */
    private String _lng;
    /** 緯度 */
    private String _lat;
    /** 接続ユーザ数 */
    private int _connectionUsers;
    /** 接続状態 */
    private String _connectionStatus;

    /**
     * 認証キーを設定する
     * @param authKey 認証キー
     */
    public void setAuthKey(String authKey) {
        _authKey = authKey;
    }

    /**
     * 認証キーを返却する
     * @return 認証キー
     */
    public String getAuthKey() {
        return _authKey;
    }

    /**
     * リクエストIDを設定する
     * @param requestId リクエストID
     */
    public void setRequestId(String requestId) {
        _requestId = requestId;
    }

    /**
     * リクエストIDを返却する
     * @return リクエストID
     */
    public String getRequestId() {
        return _requestId;
    }

    /**
     * コネクションIDを設定する
     * @param authKey 認証キー
     */
    public void setConnectionId(String authKey) {
        _connectionId = authKey;
    }

    /**
     * コネクションIDを返却する
     * @return 認証キー
     */
    public String getConnectionId() {
        return _connectionId;
    }

    /**
     * 経度を返却する
     * @return 経度
     */
    public String getLng() {
        return _lng;
    }

    /**
     * 経度を設定する
     * @param lng 経度
     */
    public void setLng(String lng) {
        _lng = lng;
    }

    /**
     * 緯度を返却する
     * @return 緯度
     */
    public String getLat() {
        return _lat;
    }

    /**
     * 緯度を設定する
     * @param lat 緯度
     */
    public void setLat(String lat) {
        _lat = lat;
    }

    /**
     * 接続ユーザ数を返却する
     * @return 接続ユーザ数
     */
    public int getConnectionUsers() {
        return _connectionUsers;
    }

    /**
     * 接続ユーザ数を設定する
     * @param connectionUsers 接続ユーザ数
     */
    public void setConnectionUsers(int connectionUsers) {
        _connectionUsers = connectionUsers;
    }

    /**
     * ユーザの接続状態を返却する
     * @return ユーザの接続状態
     */
    public String getConnectionStatus() {
        return _connectionStatus;
    }

    /**
     * ユーザの接続状態を設定する
     * @param connectionStatus ユーザの接続状態
     */
    public void setConnectionStatus(String connectionStatus) {
        _connectionStatus = connectionStatus;
    }
}
