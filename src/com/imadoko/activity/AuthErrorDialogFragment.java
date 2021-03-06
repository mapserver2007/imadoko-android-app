package com.imadoko.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import com.imadoko.R;

public class AuthErrorDialogFragment extends DialogFragment {
    private Dialog _dialog;
    private String _message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ErrorDialogTheme));
        _dialog = builder
            .setMessage(_message)
            .setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((SplashActivity) getActivity()).finish();
                    dialog.dismiss();
                }
            })
            .create();
        _dialog.setCanceledOnTouchOutside(true);

        return _dialog;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }

    public void setMessage(String message) {
        _message = message;
    }
}
