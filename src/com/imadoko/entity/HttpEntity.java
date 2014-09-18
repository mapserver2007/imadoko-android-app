package com.imadoko.entity;

import java.util.Map;

/**
 * アプリケーション定数クラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class HttpEntity {
    /** リクエストURL */
    private String _url;
    /** リクエストパラメータ */
    private Map<String, String> _params;

    /**
     * URLを返却する
     * @return URL
     */
    public String getUrl() {
        return _url;
    }

    /**
     * URLを設定する
     * @param url URL
     */
    public void setUrl(String url) {
        _url = url;
    }

    /**
     * パラメータを返却する
     * @return パラメータ
     */
    public Map<String, String> getParams() {
        return _params;
    }

    /**
     * パラメータを設定する
     * @param params パラメータ
     */
    public void setParams(Map<String, String> params) {
        _params = params;
    }
}
