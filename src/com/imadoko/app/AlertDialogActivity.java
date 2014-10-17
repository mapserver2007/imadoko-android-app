package com.imadoko.app;

import java.util.List;

import com.imadoko.service.ConnectionReceiver;
import com.imadoko.service.ConnectionService;
import com.imadoko.util.AlertDialogFragment;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class AlertDialogActivity extends FragmentActivity {

    private ConnectionReceiver _receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.show(getSupportFragmentManager(), "alert_dialog");
    }

    public void startService(String message) {
        if (!isServiceRunning(this, ConnectionService.class)) {
            IntentFilter filter = new IntentFilter();
            _receiver = new ConnectionReceiver();
            filter.addAction(ConnectionService.ACTION);
            registerReceiver(_receiver, filter);
            startService(new Intent(this, ConnectionService.class));
            Log.d(AppConstants.TAG_APPLICATION, message);
        }
    }

    public void stopService(String message) {
        if (isServiceRunning(this, ConnectionService.class)) {
            stopService(new Intent(this, ConnectionService.class));
            unregisterReceiver(_receiver);
        }
    }

    private boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : runningService) {
            if (cls.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
