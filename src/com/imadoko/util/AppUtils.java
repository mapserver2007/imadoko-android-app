package com.imadoko.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Hex;

import com.imadoko.entity.GeofenceStatusEntity;

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
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(salt.getBytes());
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
     * @param prevPlaceId 直前の地点ID
     * @param nextPlaceId 現在の地点ID
     * @param expired 直近ログより一定時間経過しているかどうか(0:経過していない、1:経過している)
     * @return 通知可否
     */
    public static boolean isGeofenceNotification(GeofenceStatusEntity status) {
        boolean isNotify = false;

        switch (status.getNextTransitionPatternId()) {
        case 11: // in -> in
            // in -> in はありえるパターン
            // たとえば、地点Aでin→電波OFF→地点Bで電波ON→地点Bでin
            // in -> in間が異なる場所であれば通知する
            if (status.getPrevPlaceId() != status.getNextPlaceId()) {
                isNotify = true;
            }
            break;
        case 12: // in -> out
            // out -> inを時間で制限しているが逆にこちらはゆるくする
            isNotify = true;
            break;
        case 14: // in -> stay
            // 直前にout -> in(同一地点)が通知可能で発生していないと通知可能にはしない
            // 例えば、stay(地点A) -> in(地点A) = 再起動した場合 は必ずNGとなる
            // その後、out -> inで通知するのはおかしい。
            // 前回の同一地点でのstayより一定時間経過していないと通知許可にしない
            if (status.getPrevPlaceId() == status.getNextPlaceId() &&
                status.getPrevTransitionPatternId() == 21 &&
                status.getExpired() == 1) { // 前回のstayより一定時間経過している
                isNotify = true;
            }
            break;
        case 21: // out -> in
            // out(地点A) -> in(地点A)は一定時間以上経過していないと通知しない
            // これはGeofence境界をうろついた時の連続通知を防止するため
            // つまり、Geofence地点へは一定期間内に1度しか侵入できない仕様
            // expired=1ならば一定時間以上経過しているので通知する
            // out(地点B) -> in(地点)は無条件で通知する
            // ただし、ログ件数0(初回)でこのイベントが発生した場合は地点が一致しないくても例外的に通知を許可
            if (status.getPrevPlaceId() != status.getNextPlaceId() || status.getPrevPlaceId() == 0) {
                isNotify = true;
            } else {
                if (status.getExpired() == 1) { // 前回のinより一定時間経過している
                    isNotify = true;
                }
            }
            break;
        case 22: // out -> out
            break;
        case 24: // out -> stay
            break;
        case 41: // stay -> in
            // stay -> inはありえるパターン
            // stay(地点A) -> 電波OFF -> 電波ON(地点B) -> in(地点B)
            // stay -> in間が異なる場所であれば通知する
            if (status.getPrevPlaceId() != status.getNextPlaceId()) {
                isNotify = true;
            }
            break;
        case 42: // stay -> out
            isNotify = true;
            break;
        case 44: // stay -> stay
            break;
        default:
            // ログが0件の場合は初回しか発生しない
            // 遷移パターン判定せず通知可能状態とする
            isNotify = true;
            break;
        }

        return isNotify;
    }

    public static String getGeofenceStatus(int transitionType) {
        String status = "";
        switch (transitionType) {
        case AppConstants.TRANSITION_TYPE_ENTER:
            status = "in";
            break;
        case AppConstants.TRANSITION_TYPE_EXIT:
            status = "out";
            break;
        case AppConstants.TRANSITION_TYPE_DWELL:
            status = "stay";
            break;
        default:
            status = "???"; // ログなしの場合
            break;
        }

        return status;
    }

    public static TrustManager[] getTrustAllCerts() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        }};
    }
}
