package com.imadoko.service;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.imadoko.app.AppConstants;
import com.imadoko.app.AppConstants.CONNECTION;

public class GeofenceService extends IntentService {

    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            sendBroadcast(CONNECTION.GEOFENCE_ERROR);
            return;
        }

        List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
        if (triggerList == null) {
            sendBroadcast(CONNECTION.GEOFENCE_ERROR);
            return;
        }

        CONNECTION status;
        for (Geofence geofence : triggerList) {
            int transitionType = LocationClient.getGeofenceTransition(intent);

            switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                status = CONNECTION.GEOFENCE_IN;
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                status = CONNECTION.GEOFENCE_OUT;
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                status = CONNECTION.GEOFENCE_STAY;
                break;
            default:
                continue;
            }

            sendBroadcast(status, geofence.getRequestId());
        }
    }

    /**
     * BroadcastReceiverへステータスを送信
     * @param status 接続ステータス
     */
    private void sendBroadcast(CONNECTION status) {
        Intent intent = new Intent(AppConstants.ACTION);
        intent.putExtra(AppConstants.SERIVCE_MESSAGE, status);
        sendBroadcast(intent);
    }

    /**
     * BroadcastReceiverへステータスを送信
     * @param status 接続ステータス
     * @param requestId リクエストID
     */
    private void sendBroadcast(CONNECTION status, String requestId) {
        Intent intent = new Intent(AppConstants.ACTION);
        intent.putExtra(AppConstants.SERIVCE_MESSAGE, status);
        intent.putExtra(AppConstants.GEOFENCE_REQUEST_ID, requestId);
        sendBroadcast(intent);
    }
}
