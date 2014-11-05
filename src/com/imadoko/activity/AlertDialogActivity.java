package com.imadoko.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.imadoko.util.AppConstants;

public class AlertDialogActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String message = getIntent().getStringExtra(AppConstants.PARAM_DIALOG_MESSAGE);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setMessage(message);
        fragment.show(getSupportFragmentManager(), AlertDialogActivity.class.getSimpleName());
    }
}
