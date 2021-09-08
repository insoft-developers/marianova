package com.insoft.tabusearch.model;

public class Tabulist {
    private int id;
    private String nama_wisata;
    private String latitude;
    private String longitude;
    private String jarak_dari_user;
    private String jarak_ke_tujuan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getJarak_dari_user() {
        return jarak_dari_user;
    }

    public void setJarak_dari_user(String jarak_dari_user) {
        this.jarak_dari_user = jarak_dari_user;
    }

    public String getJarak_ke_tujuan() {
        return jarak_ke_tujuan;
    }

    public void setJarak_ke_tujuan(String jarak_ke_tujuan) {
        this.jarak_ke_tujuan = jarak_ke_tujuan;
    }
}
