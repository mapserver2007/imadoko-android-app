package com.imadoko.entity;

/**
 * HTTPレスポンスエンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/11/01
 */
public class HttpResponseEntity {
    /** ステータスコード */
    private int _statusCode;
    /** レスポンスボディ */
    private String _responseBody;

    /**
     * ステータスコードを返却する
     * @return ステータスコード
     */
    public int getStatusCode() {
        return _statusCode;
    }

    /**
     * ステータスコードを設定する
     * @param statusCode ステータスコード
     */
    public void setStatusCode(int statusCode) {
        _statusCode = statusCode;
    }

    /**
     * レスポンスボディを返却する
     * @return レスポンスボディ
     */
    public String getResponseBody() {
        return _responseBody;
    }

    /**
     * レスポンスボディを設定する
     * @param responseBody レスポンスボディ
     */
    public void setResponseBody(String responseBody) {
        _responseBody = responseBody;
    }
}
