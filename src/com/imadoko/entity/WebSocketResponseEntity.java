package com.imadoko.entity;

public class WebSocketResponseEntity extends WebSocketEntity {

    private String _senderId;

    private String _lng;

    private String _lat;

    public String getSenderId() {
        return _senderId;
    }

    public void setSenderId(String senderId) {
        _senderId = senderId;
    }

    public String getLng() {
        return _lng;
    }

    public void setLng(String lng) {
        _lng = lng;
    }

    public String getLat() {
        return _lat;
    }

    public void setLat(String lat) {
        _lat = lat;
    }
}
