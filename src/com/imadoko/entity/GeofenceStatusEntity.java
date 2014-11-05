package com.imadoko.entity;

/**
 * Geofence移動ステータスエンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/11/04
 */
public class GeofenceStatusEntity {
    /** 移動ステータス */
    private int _transitionType;
    /** ログ書き込み可否 */
    private int _expired;
    /** Geofence進入通知フラグ */
    private int _in;
    /** Geofence退出通知フラグ */
    private int _out;
    /** Geofence滞在通知フラグ */
    private int _stay;

    /**
     * 移動ステータスを返却する
     * @return 移動ステータス
     */
    public int getTransitionType() {
        return _transitionType;
    }

    /**
     * 移動ステータスを設定する
     * @param status 移動ステータス
     */
    public void setTransitionType(int transitionType) {
        _transitionType = transitionType;
    }

    /**
     * 直近ログより一定時間経過しているかどうかを返却する
     * @return 直近ログより一定時間経過しているかどうか
     */
    public int getExpired() {
        return _expired;
    }

    /**
     * 直近ログより一定時間経過しているかどうかを設定する
     * @param expired 直近ログより一定時間経過しているかどうか
     */
    public void setExpired(int expired) {
        _expired = expired;
    }

    /**
     * Geofence進入通知フラグを返却する
     * @return Geofence進入通知フラグ
     */
    public int getIn() {
        return _in;
    }

    /**
     * Geofence進入通知フラグを設定する
     * @param in Geofence進入通知フラグ
     */
    public void setIn(int in) {
        _in = in;
    }

    /**
     * Geofence退出通知フラグを返却する
     * @return Geofence退出通知フラグ
     */
    public int getOut() {
        return _out;
    }

    /**
     * Geofence退出通知フラグを設定する
     * @param out Geofence退出通知フラグ
     */
    public void setOut(int out) {
        _out = out;
    }

    /**
     * Geofence滞在通知フラグを返却する
     * @return Geofence滞在通知フラグ
     */
    public int getStay() {
        return _stay;
    }

    /**
     * Geofence滞在通知フラグを設定する
     * @param stay Geofence滞在通知フラグ
     */
    public void setStay(int stay) {
        _stay = stay;
    }
}
