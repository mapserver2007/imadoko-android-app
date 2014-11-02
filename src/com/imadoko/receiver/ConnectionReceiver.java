package com.imadoko.receiver;

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
        String requestId, message;
        Bundle bundle = intent.getExtras();
        CONNECTION status = (CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);

        switch (status) {
        case DISCONNECT:
            activity.onConnectionError(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case CONNECTED:
            activity.onConnected(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case RECONNECT:
            activity.onReConnecting(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case SEND_PING:
            // ログは更新するが画面上のステータスは更新しない
            activity.showDebugLog(status.toString());
            break;
        case RECEIVE_PONG:
            // 画面上のステータスは接続確立
            activity.onConnected(AppConstants.CONNECTION.CONNECTING.toString());
            activity.showDebugLog(status.toString());
            break;
        case CONNECTING:
            activity.onConnected(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case LOCATION_OK:
            activity.onConnected(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case LOCATION_NG:
            activity.onConnected(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case GEOFENCE_IN:
            // ログは更新するが画面上のステータスは更新しない
            requestId = bundle.getString(AppConstants.GEOFENCE_REQUEST_ID);
            message = activity.getLandMarkName(requestId);
            activity.showDebugLog(message + status.toString());
            break;
        case GEOFENCE_OUT:
            // ログは更新するが画面上のステータスは更新しない
            requestId = bundle.getString(AppConstants.GEOFENCE_REQUEST_ID);
            message = activity.getLandMarkName(requestId);
            activity.showDebugLog(message + status.toString());
            break;
        case GEOFENCE_STAY:
            // ログは更新するが画面上のステータスは更新しない
            requestId = bundle.getString(AppConstants.GEOFENCE_REQUEST_ID);
            message = activity.getLandMarkName(requestId);
            activity.showDebugLog(message + status.toString());
            break;
        case GEOFENCE_ERROR:
            // ログは更新するが画面上のステータスは更新しない
            activity.showDebugLog(status.toString());
            break;
        default:
            break;
        }
    }
}
