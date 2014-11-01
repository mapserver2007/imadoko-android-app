package com.imadoko.service;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.imadoko.app.AppConstants;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GeofenceService extends IntentService {

//    public static final String ACTION = "GeofenceServiceAction";

    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO 自動生成されたメソッド・スタブ
        int transitionType = LocationClient.getGeofenceTransition(intent);

        if (LocationClient.hasError(intent)) {
            int errorCode = LocationClient.getErrorCode(intent);
            return;
            // TODO
        }

        List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
        if (triggerList == null) {
            return;
        }

        for (Geofence geofence : triggerList) {

        }

        Log.d(AppConstants.TAG_SERVICE, "owata------");
    }

}
