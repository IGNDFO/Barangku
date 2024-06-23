package com.example.barangku.activity.model;

public class ModelRiwayatPengajuanBarangKeluar {
    private String id, namaBarang, satuan, keterangan, tanggalKeluar, namaKlien, alamatKlien, status;
    private long jumlahBarang;

    public ModelRiwayatPengajuanBarangKeluar(){}

    public ModelRiwayatPengajuanBarangKeluar(String id, String namaBarang, String satuan, String keterangan, String tanggalKeluar, String namaKlien, String alamatKlien, String status, long jumlahBarang) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.satuan = satuan;
        this.keterangan = keterangan;
        this.tanggalKeluar = tanggalKeluar;
        this.namaKlien = namaKlien;
        this.alamatKlien = alamatKlien;
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

    public String getTanggalKeluar() {
        return tanggalKeluar;
    }

    public void setTanggalKeluar(String tanggalKeluar) {
        this.tanggalKeluar = tanggalKeluar;
    }

    public String getNamaKlien() {
        return namaKlien;
    }

    public void setNamaKlien(String namaKlien) {
        this.namaKlien = namaKlien;
    }

    public String getAlamatKlien() {
        return alamatKlien;
    }

    public void setAlamatKlien(String alamatKlien) {
        this.alamatKlien = alamatKlien;
    }

    public long getJumlahBarang() {
        return jumlahBarang;
    }

    public void setJumlahBarang(long jumlahBarang) {
        this.jumlahBarang = jumlahBarang;
    }
}
