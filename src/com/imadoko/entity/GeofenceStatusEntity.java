package com.imadoko.entity;

/**
 * Geofence移動ステータスエンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/11/04
 */
public class GeofenceStatusEntity {
    /** 前回の地点ID */
    private int _prevPlaceId;
    /** 今回の地点ID */
    private int _nextPlaceId;
    /** 前回の移動ステータス */
    private int _prevTransitionType;
    /** 今回の移動ステータス */
    private int _nextTransitionType;
    /** 一定時間経過しているかどうか */
    private int _expired;
    /** Geofence進入通知フラグ */
    private int _in;
    /** Geofence退出通知フラグ */
    private int _out;
    /** Geofence滞在通知フラグ */
    private int _stay;
    /** 前回の移動パターン */
    private int _prevTransitionPatternId;
    /** 今愛の移動パターン */
    private int _nextTransitionPatternId;
    /** Geofence地点経度 */
    private double _landmarkLng;
    /** Geofence地点緯度 */
    private double _landmarkLat;
    /** 現在の経度 */
    private double _currentLng;
    /** 現在の緯度 */
    private double _currentLat;

    /**
     * 前回の地点IDを返却する
     * @return 地点ID
     */
    public int getPrevPlaceId() {
        return _prevPlaceId;
    }

    /**
     * 前回の地点IDを設定する
     * @param prevPlaceId 前回の地点ID
     */
    public void setPrevPlaceId(int prevPlaceId) {
        _prevPlaceId = prevPlaceId;
    }

    /**
     * 今回の地点IDを返却する
     * @return 今回の地点ID
     */
    public int getNextPlaceId() {
        return _nextPlaceId;
    }

    /**
     * 今回の地点IDを設定する
     * @param _nextPlaceId 今回の地点ID
     */
    public void setNextPlaceId(int nextPlaceId) {
        _nextPlaceId = nextPlaceId;
    }

    /**
     * 前回の移動ステータスを返却する
     * @return 前回の移動ステータス
     */
    public int getPrevTransitionType() {
        return _prevTransitionType;
    }

    /**
     * 前回の移動ステータスを設定する
     * @param prevTransitionType 前回の移動ステータス
     */
    public void setPrevTransitionType(int prevTransitionType) {
        _prevTransitionType = prevTransitionType;
    }

    /**
     * 移動ステータスを返却する
     * @return 移動ステータス
     */
    public int getNextTransitionType() {
        return _nextTransitionType;
    }

    /**
     * 移動ステータスを設定する
     * @param nextTransitionType 移動ステータス
     */
    public void setNextTransitionType(int nextTransitionType) {
        _nextTransitionType = nextTransitionType;
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

    /**
     * 前回の移動パターンを返却する
     * @return 前回の移動パターン
     */
    public int getPrevTransitionPatternId() {
        return _prevTransitionPatternId;
    }

    /**
     * 前回の移動パターンを設定する
     * @param prevTransitionPatternId 前回の移動パターン
     */
    public void setPrevTransitionPatternId(int prevTransitionPatternId) {
        _prevTransitionPatternId = prevTransitionPatternId;
    }

    /**
     * 今回の移動パターンを返却する
     * @return 今回の移動パターン
     */
    public int getNextTransitionPatternId() {
        return _nextTransitionPatternId;
    }

    /**
     * 今回の移動パターンを設定する
     * @param nextTransitionPatternId 今回の移動パターン
     */
    public void setNextTransitionPatternId(int nextTransitionPatternId) {
        _nextTransitionPatternId = nextTransitionPatternId;
    }

    /**
     * Geofence地点経度を返却する
     * @return Geofence地点経度
     */
    public double getLandmarkLng() {
        return _landmarkLng;
    }

    /**
     * Geofence地点経度を設定する
     * @param _landmarkLng Geofence地点経度
     */
    public void setLandmarkLng(double _landmarkLng) {
        this._landmarkLng = _landmarkLng;
    }

    /**
     * Geofence地点緯度を返却する
     * @return Geofence地点緯度
     */
    public double getLandmarkLat() {
        return _landmarkLat;
    }

    /**
     * Geofence地点緯度を設定する
     * @param _landmarkLat Geofence地点緯度
     */
    public void setLandmarkLat(double _landmarkLat) {
        this._landmarkLat = _landmarkLat;
    }

    /**
     * 現在の経度を返却する
     * @return 現在の経度
     */
    public double getCurrentLng() {
        return _currentLng;
    }

    /**
     * 現在の経度を設定する
     * @param _currentLng 現在の経度
     */
    public void setCurrentLng(double _currentLng) {
        this._currentLng = _currentLng;
    }

    /**
     * 現在の緯度を返却する
     * @return 現在の緯度
     */
    public double getCurrentLat() {
        return _currentLat;
    }

    /**
     * 現在の緯度を設定する
     * @param _currentLat 現在の緯度
     */
    public void setCurrentLat(double _currentLat) {
        this._currentLat = _currentLat;
    }
}
