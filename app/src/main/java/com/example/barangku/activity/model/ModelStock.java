package com.example.barangku.activity.model;

public class ModelStock {
    private String namaBarang, jumlahBarang, satuan, gambar;
    public ModelStock(){}
    public ModelStock(String namaBarang, String jumlahBarang, String satuan, String gambar) {
        this.namaBarang = namaBarang;
        this.jumlahBarang = jumlahBarang;
        this.satuan = satuan;
        this.gambar = gambar;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getJumlahBarang() {
        return jumlahBarang;
    }

    public String getSatuan() {
        return satuan;
    }

    public String getGambar() {
        return gambar;
    }
}
