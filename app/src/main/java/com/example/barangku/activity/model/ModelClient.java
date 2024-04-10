package com.example.barangku.activity.model;

public class ModelClient {
    String nama, alamat, email,  catatan;
    String no_telp;


    public ModelClient(String nama, String alamat, String email, String no_telp, String catatan) {
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

    public  String getNo_telp() {
        return no_telp;
    }

    public void setNo_telp(String no_telp) {
        this.no_telp = no_telp;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}