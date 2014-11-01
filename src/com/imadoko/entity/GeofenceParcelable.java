package com.imadoko.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Geofence用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class GeofenceParcelable implements Parcelable {
    /** 経度 */
    private String _lng;
    /** 緯度 */
    private String _lat;
    /** 半径 */
    private String _radius;
    /** 滞在時間 */
    private long _loiteringDelay;
    /** 住所 */
    private String _address;
    /** ランドマーク名 */
    private String _landmark;

    /**
     * 経度を返却する
     * @return 経度
     */
    public String getLng() {
        return _lng;
    }

    /**
     * 経度を設定する
     * @param lng 経度
     */
    public void setLng(String lng) {
        _lng = lng;
    }

    /**
     * 緯度を返却する
     * @return 緯度
     */
    public String getLat() {
        return _lat;
    }

    /**
     * 緯度を設定する
     * @param lat 緯度
     */
    public void setLat(String lat) {
        _lat = lat;
    }

    /**
     * 半径を返却する
     * @return 半径
     */
    public String getRadius() {
        return _radius;
    }

    /**
     * 半径を設定する
     * @param radius 半径
     */
    public void setRadius(String radius) {
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

    /**
     * 住所を返却する
     * @return 住所
     */
    public String getAddress() {
        return _address;
    }

    /**
     * 住所を設定する
     * @param address 住所
     */
    public void setAddress(String address) {
        _address = address;
    }

    /**
     * ランドマーク名を返却する
     * @return ランドマーク名
     */
    public String getLandmark() {
        return _landmark;
    }

    /**
     * ランドマーク名を設定する
     * @param landmarkName ランドマーク名
     */
    public void setLandmark(String landmark) {
        _landmark = landmark;
    }

    public GeofenceParcelable() {}

    public GeofenceParcelable(Parcel in) {
        String[] stringArray = new String[5];
        in.readStringArray(stringArray);

        _lng = stringArray[0];
        _lat = stringArray[1];
        _radius = stringArray[2];
        _address = stringArray[3];
        _landmark = stringArray[4];
        _loiteringDelay = in.readLong();
    }

    public static final Parcelable.Creator<GeofenceParcelable> CREATOR = new Parcelable.Creator<GeofenceParcelable>() {
        @Override
        public GeofenceParcelable createFromParcel(Parcel in) {
            return new GeofenceParcelable(in);
        }

        @Override
        public GeofenceParcelable[] newArray(int size) {
            return new GeofenceParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[] {
            _lng, _lat, _radius, _address, _landmark
        });
        out.writeLong(_loiteringDelay);
    }
}
