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
import android.widget.EditText;

import com.imadoko.app.R;

public class SettingsDialogFragment extends DialogFragment {
    private Dialog _dialog;
    private View _layout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ErrorDialogTheme));
        _dialog = builder
            .setMessage("表示するユーザ名を入力")
            .setView(_layout)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText = (EditText) _layout.findViewById(R.id.dialog_edittext);
                    String userName = ((SpannableStringBuilder) editText.getText()).toString();
                    ((MainActivity) getActivity()).registerUserName(userName);
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
}
