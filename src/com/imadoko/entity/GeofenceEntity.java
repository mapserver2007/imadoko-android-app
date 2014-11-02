package com.imadoko.entity;

import java.util.ArrayList;

/**
 * GeofenceEntity用エンティティクラス
 * @author Ryuichi Tanaka
 * @since 2014/11/01
 */
public class GeofenceEntity {
    /** Geofenceデータ */
    private ArrayList<GeofenceParcelable> _data;

    /**
     * Geofenceデータを返却する
     * @return
     */
    public ArrayList<GeofenceParcelable> getData() {
        return _data;
    }

    /**
     * Geofenceデータを設定する
     * @param data Geofenceデータ
     */
    public void setData(ArrayList<GeofenceParcelable> data) {
        _data = data;
    }
}
