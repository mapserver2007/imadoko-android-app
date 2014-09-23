package com.imadoko.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imadoko.app.AppConstants;
import com.imadoko.app.MainActivity;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity activity = (MainActivity) context;
        Bundle bundle = intent.getExtras();
        AppConstants.CONNECTION status = (AppConstants.CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);

        if (AppConstants.CONNECTION.AUTH_NG == status) {
            activity.onConnectionError("認証失敗");
        } else if (AppConstants.CONNECTION.DISCONNECT == status) {
            activity.onConnectionError("WebSocket切断");
        } else if (AppConstants.CONNECTION.CONNECTED == status) {
            activity.onConnected("WebSocket開始");
        } else if (AppConstants.CONNECTION.RECONNECT == status) {
            activity.onConnectionError("WebSocket再接続");
        } else if (AppConstants.CONNECTION.CONNECTING == status) {
            activity.onConnected("WebSocket接続確立");
        } else if (AppConstants.CONNECTION.LOCATION_OK == status) {
            activity.onConnected("位置情報取得成功");
        } else if (AppConstants.CONNECTION.LOCATION_NG == status) {
            activity.onConnected("位置情報取得失敗");
        }
    }
}
