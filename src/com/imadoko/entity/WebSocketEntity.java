package com.imadoko.entity;

public class WebSocketEntity {
    protected String _authKey;

    protected String _requestId;


    public void setAuthKey(String authKey) {
        _authKey = authKey;
    }

    public String getAuthKey() {
        return _authKey;
    }

    public void setRequestId(String requestId) {
        _requestId = requestId;
    }

    public String getRequestId() {
        return _requestId;
    }
}
