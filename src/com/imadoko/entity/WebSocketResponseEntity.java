package com.imadoko.entity;

public class WebSocketResponseEntity extends WebSocketEntity {

    private String _senderId;

    private String _userId;

    private double _lng;

    private double _lat;

    public String getSenderId() {
        return _senderId;
    }

    public void setSenderId(String senderId) {
        _senderId = senderId;
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public double getLng() {
        return _lng;
    }

    public void setLng(double lng) {
        _lng = lng;
    }

    public double getLat() {
        return _lat;
    }

    public void setLat(double lat) {
        _lat = lat;
    }
}
