package com.example.barangku.activity.model;

public class ModelStock {
    private String id, namaBarang, jumlahBarang, satuan, gambar;
    private  Boolean enable;
    public ModelStock(){}

    public ModelStock(String id, String namaBarang, String jumlahBarang, String satuan, String gambar, Boolean enable) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.jumlahBarang = jumlahBarang;
        this.satuan = satuan;
        this.gambar = gambar;
        this.enable = enable;
    }

    public Boolean getEnable() {
        return enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getJumlahBarang() {
        return jumlahBarang;
    }

    public void setJumlahBarang(String jumlahBarang) {
        this.jumlahBarang = jumlahBarang;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
