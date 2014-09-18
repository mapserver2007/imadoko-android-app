package com.imadoko.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import android.util.Log;
import com.imadoko.app.AppConstants;

/**
 * 認証を実行する
 * @author Ryuichi Tanaka
 * @since 2014/09/05
 */
public class AuthManager {
    /** 個体識別番号 */
    private String _IMEI;

    /** salt */
    private String _salt;

    /**
     * コンストラクタ
     * @param IMEI 個体識別番号
     * @param salt ソルトキー
     */
    public AuthManager(String IMEI, String salt) {
        _IMEI = IMEI;
        _salt = salt;
    }

    /**
     * 認証キーを返却する
     * @return 認証キー
     */
    public String generateAuthKey() {
        MessageDigest md;
        String authKey = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(_IMEI).append(_salt);
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(new String(sb).getBytes());
            authKey = String.valueOf(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            Log.e(AppConstants.TAG_APPLICATION, e.getMessage());
        }

        return authKey;
    }
}
