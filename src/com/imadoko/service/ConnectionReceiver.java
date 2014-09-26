package com.imadoko.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imadoko.app.AppConstants;
import com.imadoko.app.AppConstants.CONNECTION;
import com.imadoko.app.MainActivity;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity activity = (MainActivity) context;
        Bundle bundle = intent.getExtras();
        CONNECTION status = (AppConstants.CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);

        switch (status) {
        case AUTH_NG:
            activity.onConnectionError("認証失敗");
            break;
        case DISCONNECT:
            activity.onConnectionError("WebSocket切断");
            break;
        case CONNECTED:
            activity.onConnected("WebSocket開始");
            break;
        case RECONNECT:
            activity.onConnectionError("WebSocket再接続");
            break;
        case CONNECTING:
            activity.onConnected("WebSocket接続中");
            break;
        case LOCATION_OK:
            activity.onConnected("位置情報取得成功");
            break;
        case LOCATION_NG:
            activity.onConnected("位置情報取得失敗");
            break;
        default:
            break;
        }
    }
}
