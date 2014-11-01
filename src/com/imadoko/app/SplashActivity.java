package com.imadoko.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.imadoko.entity.GeofenceEntity;
import com.imadoko.entity.GeofenceParcelable;
import com.imadoko.entity.HttpRequestEntity;
import com.imadoko.entity.HttpResponseEntity;
import com.imadoko.network.AsyncHttpTaskLoader;
import com.imadoko.util.AuthErrorDialogFragment;
import com.imadoko.util.AuthManager;

public class SplashActivity extends FragmentActivity {

    private String _authKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        executeAuth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startMainActivity(ArrayList<GeofenceParcelable> geofenceList) {
        for (GeofenceParcelable geofence : geofenceList) {
            geofence.setLoiteringDelay(AppConstants.LOITERING_DELAY);
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppConstants.PARAM_GEOFENCE_ENTITY, geofenceList);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(AppConstants.PARAM_AUTH_KEY, _authKey);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            HttpRequestEntity entity = new HttpRequestEntity();
            entity.setUrl(AppConstants.MASTER_GEOFENCE_URL);
            Map<String, String> params = new HashMap<String, String>();
            params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
            entity.setParams(params);

            AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
                @Override
                public void deliverResult(HttpResponseEntity response) {
                    GeofenceEntity geofenceEntity = JSON.decode(response.getResponseBody(), GeofenceEntity.class);
                    startMainActivity(geofenceEntity.getData());
                }
            };
            loader.get();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        AuthErrorDialogFragment dialog = new AuthErrorDialogFragment();
        dialog.show(getSupportFragmentManager(), AppConstants.DIALOG_AUTH_ERROR);
    }

    private void executeAuth() {
        _authKey = getAuthKey();
        HttpRequestEntity entity = new HttpRequestEntity();
        entity.setUrl(AppConstants.AUTH_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                onAuthResult(response.getStatusCode());
            }
        };
        loader.post();
    }

    private String getAuthKey() {
        String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AuthManager manager = new AuthManager(udid, AppConstants.SECURITY_SALT);
        return manager.generateAuthKey();
    }
}
