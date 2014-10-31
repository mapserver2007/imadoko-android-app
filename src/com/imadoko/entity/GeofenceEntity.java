package com.imadoko.entity;

/**
 * Geofence用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class GeofenceEntity {
    /** 経度 */
    private double _lng;
    /** 緯度 */
    private double _lat;
    /** 半径 */
    private float _radius;
    /** 滞在時間 */
    private long _loiteringDelay;

    /**
     * 経度を返却する
     * @return 経度
     */
    public double getLng() {
        return _lng;
    }

    /**
     * 経度を設定する
     * @param lng 経度
     */
    public void setLng(double lng) {
        _lng = lng;
    }

    /**
     * 緯度を返却する
     * @return 緯度
     */
    public double getLat() {
        return _lat;
    }

    /**
     * 緯度を設定する
     * @param lat 緯度
     */
    public void setLat(double lat) {
        _lat = lat;
    }

    /**
     * 半径を返却する
     * @return 半径
     */
    public float getRadius() {
        return _radius;
    }

    /**
     * 半径を設定する
     * @param radius 半径
     */
    public void setRadius(float radius) {
        _radius = radius;
    }

    /**
     * 滞在時間を返却する
     * @return 滞在時間
     */
    public long getLoiteringDelay() {
        return _loiteringDelay;
    }

    /**
     * 滞在時間を設定する
     * @param loiteringDelay 滞在時間
     */
    public void setLoiteringDelay(long loiteringDelay) {
        _loiteringDelay = loiteringDelay;
    }
}
