package com.helloworld.loginscreen.db;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class UserAuth {

    private int id;
    private String username;
    private String phone;
    private String mail;
    private String password;
    private char gender;
    private byte[] image;

    public UserAuth(int id, String username, String phone, String mail, String password, char gender, byte[] image) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.mail = mail;
        this.password = password;
        this.gender = gender;
        this.image = image;
    }

    public UserAuth(String username, String phone, String mail, String password, char gender, byte[] image) {
        this.username = username;
        this.phone = phone;
        this.mail = mail;
        this.password = password;
        this.gender = gender;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public char getGender() {
        return gender;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
