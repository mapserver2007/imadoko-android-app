package com.imadoko.entity;

/**
 * WebSocket用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class WebSocketEntity {
    /** 認証キー */
    protected String _authKey;
    /** リクエストID */
    protected String _requestId;

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
}
