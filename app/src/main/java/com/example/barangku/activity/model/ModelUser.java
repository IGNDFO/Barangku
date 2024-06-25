package com.example.barangku.activity.model;

public class ModelUser {
    private String id, email, nama, jabatan, token;

    public ModelUser(){}

    public ModelUser(String id, String email, String nama, String jabatan, String token) {
        this.id = id;
        this.email = email;
        this.nama = nama;
        this.jabatan = jabatan;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
