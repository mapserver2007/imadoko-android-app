package com.imadoko.service;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Vibrator;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.imadoko.util.AppConstants;
import com.imadoko.util.AppConstants.CONNECTION;

/**
 * GeofenceService
 * @author Ryuichi Tanaka
 * @since 2014/11/01
 */
public class GeofenceService extends IntentService {
    /**
     * コンストラクタ
     */
    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Intent notifyIntent = new Intent(AppConstants.ACTION);

        if (LocationClient.hasError(intent)) {
            notifyIntent.putExtra(AppConstants.SERIVCE_MESSAGE, CONNECTION.GEOFENCE_ERROR);
            sendBroadcast(notifyIntent);
            return;
        }

        List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
        if (triggerList == null) {
            notifyIntent.putExtra(AppConstants.SERIVCE_MESSAGE, CONNECTION.GEOFENCE_ERROR);
            sendBroadcast(notifyIntent);
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

            notifyIntent.putExtra(AppConstants.SERIVCE_MESSAGE, status);
            notifyIntent.putExtra(AppConstants.TRANSITION_TYPE, transitionType);
            notifyIntent.putExtra(AppConstants.GEOFENCE_PLACE_ID, Integer.parseInt(geofence.getRequestId()));

            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(AppConstants.VABRATION_TIME);

            sendBroadcast(notifyIntent);
        }
    }
}
