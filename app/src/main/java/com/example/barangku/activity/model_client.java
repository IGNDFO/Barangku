package com.example.barangku.activity;

public class model_client {
    String nama, alamat, email,  catatan;
    Long no_telp;


    model_client() {
    }

    public model_client(String nama, String alamat, String email, Long no_telp, String catatan) {
        this.nama = nama;
        this.alamat = alamat;
        this.email = email;
        this.no_telp = no_telp;
        this.catatan = catatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(Long no_telp) {
        this.no_telp = no_telp;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}