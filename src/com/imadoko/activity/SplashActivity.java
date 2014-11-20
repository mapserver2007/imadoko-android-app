package com.imadoko.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.imadoko.app.R;
import com.imadoko.entity.AuthSaltEntity;
import com.imadoko.entity.GeofenceEntity;
import com.imadoko.entity.GeofenceParcelable;
import com.imadoko.entity.HttpRequestEntity;
import com.imadoko.entity.HttpResponseEntity;
import com.imadoko.network.AsyncHttpTaskLoader;
import com.imadoko.util.AppConstants;
import com.imadoko.util.AppUtils;

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
        // geofenceListが空の場合(マスタに1件もない場合)を想定していない
        // 作りこむならService側でも対処が必要
        String userName = geofenceList.get(0).getUsername();
        boolean isLocationPermission = geofenceList.get(0).getPermission() == 1;
        for (GeofenceParcelable geofence : geofenceList) {
            geofence.setRequestId(String.valueOf(geofence.getId())); // placeId -> requestId
            geofence.setLoiteringDelay(AppConstants.LOITERING_DELAY);
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppConstants.PARAM_GEOFENCE_ENTITY, geofenceList);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(AppConstants.PARAM_AUTH_KEY, _authKey);
        intent.putExtra(AppConstants.PARAM_USERNAME, userName);
        intent.putExtra(AppConstants.PARAM_LOCATION_PERMISSION, isLocationPermission);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            HttpRequestEntity entity = new HttpRequestEntity();
            entity.setUrl(AppConstants.GEOFENCE_DATA_URL);
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
        HttpRequestEntity entity1 = new HttpRequestEntity();
        entity1.setUrl(AppConstants.AUTHSALT_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_SALT_NAME, "main");
        entity1.setParams(params);

        AsyncHttpTaskLoader loader1 = new AsyncHttpTaskLoader(this, entity1) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    AuthSaltEntity authSaltEntity = JSON.decode(response.getResponseBody(), AuthSaltEntity.class);
                    String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    _authKey = AppUtils.generateAuthKey(udid, authSaltEntity.getSalt());

                    HttpRequestEntity entity2 = new HttpRequestEntity();
                    entity2.setUrl(AppConstants.AUTH_URL);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
                    entity2.setParams(params);

                    AsyncHttpTaskLoader loader2 = new AsyncHttpTaskLoader(SplashActivity.this, entity2) {
                        @Override
                        public void deliverResult(HttpResponseEntity response) {
                            onAuthResult(response.getStatusCode());
                        }
                    };
                    loader2.post();
                } else {
                    onAuthResult(response.getStatusCode());
                }
            }
        };
        loader1.get();
    }
}
