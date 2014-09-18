package com.imadoko.async;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.imadoko.entity.HttpEntity;
import com.imadoko.network.HttpClient;

/**
 * HttpRequest用AsyncTaskLoaderクラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class AsyncHttpTaskLoader extends AsyncTaskLoader<Integer> {
    /** エンティティ */
    private HttpEntity _entity;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param entity エンティティ
     */
    public AsyncHttpTaskLoader(Context context, HttpEntity entity) {
        super(context);
        _entity = entity;
    }

    /**
     * コールバック処理
     */
    @Override
    public Integer loadInBackground() {
        HttpClient client = new HttpClient();
        return client.get(_entity.getUrl(), _entity.getParams()).getStatusCode();
    }
}
