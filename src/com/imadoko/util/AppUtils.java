package com.imadoko.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import android.util.Log;

public class AppUtils {

    /**
     * 個体識別番号＋Saltから認証キーを返却する
     * @return 認証キー
     */
    public static String generateAuthKey(String imei, String salt) {
        MessageDigest md;
        String authKey = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(imei).append(salt);
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(new String(sb).getBytes());
            authKey = String.valueOf(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            Log.e(AppConstants.TAG_APPLICATION, e.getMessage());
        }

        return authKey;
    }

    /**
     * Geofence通知するかどうか
     * @param prevTransitionType 直前のGeofence移動ステータス
     * @param nextTransitionType 現在のGeofence移動ステータス
     * @param expired 直近ログより一定時間経過しているかどうか(0:経過していない、1:経過している)
     * @return 通知可否
     */
    public static boolean isGeofenceNotification(int prevTransitionType, int nextTransitionType, int expired) {
        int n = prevTransitionType * 10 + nextTransitionType;
        boolean isNotify = false;

        // TODO
        // expiredは現在2時間にしているが、意味が無いかもしれない
        // expiredの本質は、Geofence境界でIn/Outイベントが頻発することを防止することなので修正が必要

        switch (n) {
        case 11: // in -> in
            break;
        case 12: // in -> out
            isNotify = true;
            break;
        case 14: // in -> stay
            isNotify = true;
            break;
        case 21: // out -> in
            isNotify = true;
            break;
        case 22: // out -> out
            break;
        case 24: // out -> stay
            break;
        case 41: // stay -> in
            break;
        case 42: // stay -> out
            isNotify = true;
            break;
        case 44: // stay -> stay
            break;
        }

        return isNotify;
    }


}
