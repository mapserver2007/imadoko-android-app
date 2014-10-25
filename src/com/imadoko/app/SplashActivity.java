package com.imadoko.app;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.imadoko.entity.HttpEntity;
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

    public void onFinish() {
        finish();
    }

    private void onAuthResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(AppConstants.PARAM_AUTH_KEY, _authKey);
            startActivity(intent);
            finish();
        } else {
            showErrorDialog(AppConstants.CONNECTION.AUTH_NG.toString());
        }
    }

    private void showErrorDialog(String message) {
        AuthErrorDialogFragment dialog = new AuthErrorDialogFragment();
        dialog.show(getSupportFragmentManager(), AppConstants.DIALOG_AUTH_ERROR);
    }

    private void executeAuth() {
        _authKey = getAuthKey();
        HttpEntity entity = new HttpEntity();
        entity.setUrl(AppConstants.AUTH_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(Integer statusCode) {
                onAuthResult(statusCode);
            }
        };
        loader.forceLoad();
    }

    private String getAuthKey() {
        String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        AuthManager manager = new AuthManager(udid, AppConstants.SECURITY_SALT);
        return manager.generateAuthKey();
    }
}
