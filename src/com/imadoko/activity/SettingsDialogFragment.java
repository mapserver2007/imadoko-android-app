package com.imadoko.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.imadoko.R;

public class SettingsDialogFragment extends DialogFragment {
    private Dialog _dialog;
    private View _layout;
    private String _userName;
    private boolean _isLocationPermission;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ErrorDialogTheme));
        ((EditText) _layout.findViewById(R.id.dialog_edittext)).setText(_userName);
        ((CheckBox) _layout.findViewById(R.id.dialog_checkbox)).setChecked(_isLocationPermission);

        _dialog = builder
            .setMessage("imakodo設定")
            .setView(_layout)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText = (EditText) _layout.findViewById(R.id.dialog_edittext);
                    CheckBox checkBox = (CheckBox) _layout.findViewById(R.id.dialog_checkbox);
                    String userName = ((SpannableStringBuilder) editText.getText()).toString();
                    boolean isLocationPermission = checkBox.isChecked();
                    ((MainActivity) getActivity()).updateSetting(userName, isLocationPermission);
                    dialog.dismiss();
                }
            })
            .setNegativeButton("キャンセル", null)
            .create();

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
        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
        }
    }

    public void setLayout(View layout) {
        _layout = layout;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public void setPermissionLocation(boolean isPermissionLocation) {
        _isLocationPermission = isPermissionLocation;
    }
}
