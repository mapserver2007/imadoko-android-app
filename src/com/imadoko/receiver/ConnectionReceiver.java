package com.imadoko.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imadoko.activity.MainActivity;
import com.imadoko.util.AppConstants;
import com.imadoko.util.AppConstants.CONNECTION;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = (MainActivity) context;
        String message;
        int placeId;
        int transitionType;
        int connectionUsers;

        Bundle bundle = intent.getExtras();
        CONNECTION status = (CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);

        switch (status) {
        case DISCONNECT:
            activity.onConnectionError(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case CONNECTED:
        case CONNECTING:
        case LOCATION_OK:
        case LOCATION_NG:
            activity.onConnected(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case RECONNECT:
            activity.onReConnect(status.toString());
            activity.showDebugLog(status.toString());
            break;
        case RECONNECTING:
            activity.onReConnecting(status.toString());
            break;
        case SEND_PING:
            break;
        case GEOFENCE_ERROR:
        case LOCATION_UPDATE:
            // ログは更新するが画面上のステータスは更新しない
            activity.showDebugLog(status.toString());
            break;
        case RECEIVE_PONG:
            // 画面上のステータスは接続確立
            activity.onConnected(CONNECTION.CONNECTING.toString());
            break;
        case GEOFENCE_IN:
        case GEOFENCE_OUT:
        case GEOFENCE_STAY:
            placeId = bundle.getInt(AppConstants.GEOFENCE_PLACE_ID);
            transitionType = bundle.getInt(AppConstants.TRANSITION_TYPE);
            message = activity.getLandMarkName(placeId);
            activity.onGeofence(placeId, transitionType);
            activity.showDebugLog(message + status.toString());
            break;
        case USER_CONNECT:
            connectionUsers = bundle.getInt(AppConstants.CONNECTION_USERS);
            activity.onUserConnect(CONNECTION.USER_CONNECT.toString(), connectionUsers);
            break;
        case USER_DISCONNECT:
            connectionUsers = bundle.getInt(AppConstants.CONNECTION_USERS);
            activity.onUserConnect(CONNECTION.USER_DISCONNECT.toString(), connectionUsers);
            break;
        default:
            break;
        }
    }
}
