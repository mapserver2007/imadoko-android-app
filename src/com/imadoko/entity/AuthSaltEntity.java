package com.imadoko.entity;

/**
 * AuthSaltEntity
 * @author Ryuichi Tanaka
 * @since 2014/11/08
 */
public class AuthSaltEntity {
    /** salt */
    private String _salt;

    /**
     * saltを返却する
     * @return salt
     */
    public String getSalt() {
        return _salt;
    }

    /**
     * saltを設定する
     * @param salt
     */
    public void setSalt(String salt) {
        _salt = salt;
    }
}
