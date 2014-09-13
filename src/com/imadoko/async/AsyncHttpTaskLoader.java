package com.imadoko.async;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.imadoko.entity.HttpEntity;
import com.imadoko.network.HttpClient;

public class AsyncHttpTaskLoader extends AsyncTaskLoader<Integer> {
    
    private HttpEntity _entity;
    
    public AsyncHttpTaskLoader(Context context, HttpEntity entity) {
        super(context);
        _entity = entity;
    }

    @Override
    public Integer loadInBackground() {
        HttpClient client = new HttpClient();
        return client.get(_entity.getUrl(), _entity.getParams()).getStatusCode();
    }
    
}
