package com.helloworld.loginscreen.db;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class UserAuth {

    private int id;
    private String username;
    private String mail;
    private String password;
    private char gender;
    private String image_file;

    public UserAuth(int id, String username, String mail, String password, char gender, String image_file) {
        this.id = id;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.gender = gender;
        this.image_file = image_file;
    }

    public UserAuth(String username, String mail, String password, char gender, String image_file) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.gender = gender;
        this.image_file = image_file;
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

    public String getImage_file() {
        return image_file;
    }

    public void setImage_file(String image_file) {
        this.image_file = image_file;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }
}
