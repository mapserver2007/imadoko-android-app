package com.imadoko.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Geofence用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/10/31
 */
public class GeofenceParcelable implements Parcelable {
    /** PlaceID */
    private int _id;
    /** リクエストID */
    private String _requestId;
    /** 経度 */
    private String _lng;
    /** 緯度 */
    private String _lat;
    /** 半径 */
    private String _radius;
    /** 滞在時間 */
    private int _loiteringDelay;
    /** 住所 */
    private String _address;
    /** ランドマーク名 */
    private String _landmark;
    /** ユーザ名 */
    private String _username;

    /**
     * 地点IDを返却する
     * @return 地点ID
     */
    public int getId() {
        return _id;
    }

    /**
     * 地点IDを設定する
     * @param placeid 地点ID
     */
    public void setId(int id) {
        _id = id;
    }

    /**
     * リクエストIDを返却する
     * @return リクエストID
     */
    public String getRequestId() {
        return _requestId;
    }

    /**
     * リクエストIDを設定する
     * @param requestId リクエストID
     */
    public void setRequestId(String requestId) {
        _requestId = requestId;
    }

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
    public int getLoiteringDelay() {
        return _loiteringDelay;
    }

    /**
     * 滞在時間を設定する
     * @param loiteringDelay 滞在時間
     */
    public void setLoiteringDelay(int loiteringDelay) {
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

    /**
     * ユーザ名を返却する
     * @return ユーザ名
     */
    public String getUsername() {
        return _username;
    }

    /**
     * ユーザ名を設定する
     * @param username ユーザ名
     */
    public void setUsername(String username) {
        _username = username;
    }

    public GeofenceParcelable() {}

    public GeofenceParcelable(Parcel in) {
        String[] stringArray = new String[7];
        in.readStringArray(stringArray);

        _requestId = stringArray[0];
        _lng = stringArray[1];
        _lat = stringArray[2];
        _radius = stringArray[3];
        _address = stringArray[4];
        _landmark = stringArray[5];
        _username = stringArray[6];
        _loiteringDelay = in.readInt();
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
            _requestId, _lng, _lat, _radius, _address, _landmark, _username
        });
        out.writeInt(_loiteringDelay);
    }
}
