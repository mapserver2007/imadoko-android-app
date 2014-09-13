package com.imadoko.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imadoko.app.AppConstants;
import com.imadoko.app.MainActivity;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(AppConstants.SERIVCE_MESSAGE);
        
        if (AppConstants.AUTH_OK.equals(message)) {
            // 認証成功
            ((MainActivity) context).onMessage("auth ok");
        } else if (AppConstants.AUTH_NG.equals(message)) {
            // 認証失敗
            ((MainActivity) context).onServiceStop("service stop");
        } else if (AppConstants.EXCEPTION.equals(message)) {
            // 例外発生
            ((MainActivity) context).onServiceStop("error");
        } else {
            // それ以外のメッセージ
            ((MainActivity) context).onMessage(message);
        }
    }
}
