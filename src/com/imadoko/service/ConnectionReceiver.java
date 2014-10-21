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
        CONNECTION status = (CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);
        activity.showDebugLog(status.toString());

        switch (status) {
        case AUTH_NG:
            activity.onConnectionError(status.toString());
            break;
        case DISCONNECT:
            activity.onConnectionError(status.toString());
            break;
        case CONNECTED:
            activity.onConnected(status.toString());
            break;
        case RECONNECT:
            activity.onReConnecting(status.toString());
            break;
        case SEND_PING:
            // ログは更新するが画面上のステータスは更新しない
            break;
        case RECEIVE_PONG:
            // 画面上のステータスは接続確立
            activity.onConnected(AppConstants.CONNECTION.CONNECTING.toString());
            break;
        case CONNECTING:
            activity.onConnected(status.toString());
            break;
        case LOCATION_OK:
            activity.onConnected(status.toString());
            break;
        case LOCATION_NG:
            activity.onConnected(status.toString());
            break;
        default:
            break;
        }
    }
}
