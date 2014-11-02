package com.imadoko.network;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.imadoko.entity.HttpRequestEntity;
import com.imadoko.entity.HttpResponseEntity;

/**
 * HttpRequest用AsyncTaskLoaderクラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class AsyncHttpTaskLoader extends AsyncTaskLoader<HttpResponseEntity> {
    /** エンティティ */
    private HttpRequestEntity _entity;
    /** Httpクライアント */
    private HttpClient _client;
    /** 実行するHTTPメソッド */
    private METHOD _method;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param entity エンティティ
     */
    public AsyncHttpTaskLoader(Context context, HttpRequestEntity entity) {
        super(context);
        _client = new HttpClient();
        _entity = entity;
    }

    public void get() {
        _method = METHOD.GET;
        forceLoad();
    }

    public void post() {
        _method = METHOD.POST;
        forceLoad();
    }

    /**
     * コールバック処理
     */
    @Override
    public HttpResponseEntity loadInBackground() {
        HttpClient response = null;
        HttpResponseEntity entity = new HttpResponseEntity();

        if (_method == METHOD.GET) {
            response = _client.get(_entity.getUrl(), _entity.getParams());
        } else if (_method == METHOD.POST) {
            response = _client.post(_entity.getUrl(), _entity.getParams());
        }

        if (response != null) {
            entity.setStatusCode(response.getStatusCode());
            entity.setResponseBody(response.getResponseBody());
        }

        return entity;
    }

    private enum METHOD {
        GET, POST
    }
}
