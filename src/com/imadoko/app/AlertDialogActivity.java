package com.imadoko.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.imadoko.util.AlertDialogFragment;

public class AlertDialogActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.show(getSupportFragmentManager(), AppConstants.DIALOG_ALERT);
    }
}
