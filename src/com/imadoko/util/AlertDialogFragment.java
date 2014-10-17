package com.imadoko.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import com.imadoko.app.AlertDialogActivity;
import com.imadoko.app.R;

public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO ここでSerivieを停止したい

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.ErrorDialogTheme));
        Dialog dialog = builder
            .setMessage("imadokoサーバとの接続が切断されました。")
            .setPositiveButton("再接続", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO ここからServiceを起動したい
                }
            })
            .setNegativeButton("キャンセル", null)
            .create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }
}
