package com.example.barangku.activity.model;

public class ModelUser {
    private String email, nama, jabatan, token;

    public ModelUser(){}
    public ModelUser(String email, String nama, String jabatan, String token) {
        this.email = email;
        this.nama = nama;
        this.jabatan = jabatan;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getNama() {
        return nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getToken() {
        return token;
    }
}
