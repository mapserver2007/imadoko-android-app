package com.imadoko.entity;

/**
 * WebSocketレスポンスエンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class WebSocketResponseEntity extends WebSocketEntity {
    /** 送信元ID */
    private String _senderId;
    /** 経度 */
    private String _lng;
    /** 緯度 */
    private String _lat;

    /**
     * 送信元IDを返却する
     * @return 送信元ID
     */
    public String getSenderId() {
        return _senderId;
    }

    /**
     * 送信元IDを設定する
     * @param senderId 送信元ID
     */
    public void setSenderId(String senderId) {
        _senderId = senderId;
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
}
