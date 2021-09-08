package com.insoft.tabusearch.json;

public class TabusearchRequestJson {
    private int id_tujuan;
    private String origin_lat;
    private String origin_long;

    public int getId_tujuan() {
        return id_tujuan;
    }

    public void setId_tujuan(int id_tujuan) {
        this.id_tujuan = id_tujuan;
    }

    public String getOrigin_lat() {
        return origin_lat;
    }

    public void setOrigin_lat(String origin_lat) {
        this.origin_lat = origin_lat;
    }

    public String getOrigin_long() {
        return origin_long;
    }

    public void setOrigin_long(String origin_long) {
        this.origin_long = origin_long;
    }
}
