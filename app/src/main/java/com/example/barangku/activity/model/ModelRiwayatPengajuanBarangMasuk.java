package com.example.barangku.activity.model;

public class ModelRiwayatPengajuanBarangMasuk {
    private String id, namaBarang, satuan, keterangan, tanggalMasuk, status;
    private int jumlahBarang;

    public ModelRiwayatPengajuanBarangMasuk (){}

    public ModelRiwayatPengajuanBarangMasuk(String id, String namaBarang, String satuan, String keterangan, String tanggalMasuk, String status, int jumlahBarang) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.satuan = satuan;
        this.keterangan = keterangan;
        this.tanggalMasuk = tanggalMasuk;
        this.status = status;
        this.jumlahBarang = jumlahBarang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggalMasuk() {
        return tanggalMasuk;
    }

    public void setTanggalMasuk(String tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public int getJumlahBarang() {
        return jumlahBarang;
    }

    public void setJumlahBarang(int jumlahBarang) {
        this.jumlahBarang = jumlahBarang;
    }
}
