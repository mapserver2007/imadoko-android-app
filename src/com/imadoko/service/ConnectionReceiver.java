package com.imadoko.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imadoko.app.AlertDialogActivity;
import com.imadoko.app.AppConstants;
import com.imadoko.app.AppConstants.CONNECTION;
import com.imadoko.app.MainActivity;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = (MainActivity) context;
        Bundle bundle = intent.getExtras();
        CONNECTION status = (AppConstants.CONNECTION) bundle.get(AppConstants.SERIVCE_MESSAGE);

        Intent dialogIntent = new Intent(context, AlertDialogActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, dialogIntent, 0);
        try {
            if (status == AppConstants.CONNECTION.DISCONNECT) {
                pendingIntent.send();
            }
        } catch (PendingIntent.CanceledException ignore) {
        }

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
            activity.onConnectionError(status.toString());
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
