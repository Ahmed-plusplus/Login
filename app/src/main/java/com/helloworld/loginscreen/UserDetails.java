package com.helloworld.loginscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.helloworld.loginscreen.db.DBAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserDetails extends AppCompatActivity {

    SharedPreferences sp;
    DBAdapter db;
    ImageView img;
    TextView username;
    Bitmap bitmap_img;
    Thread thread;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init(savedInstanceState);

        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    startActivity(new Intent(UserDetails.this,UsersListActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void init(Bundle savedInstanceState) {
        db = new DBAdapter(this);
        sp = getSharedPreferences("Auth",MODE_PRIVATE);
        img = findViewById(R.id.user_img);
        username = findViewById(R.id.hello_user);
        if(savedInstanceState == null) {
            if (MainActivity.user.getImage() != null) {
                bitmap_img = BitmapFactory.decodeByteArray(MainActivity.user.getImage(), 0, MainActivity.user.getImage().length);
                img.setImageBitmap(bitmap_img);
                img.setBackgroundColor(0);
            } else
                bitmap_img = null;
        }else{
            bitmap_img = savedInstanceState.getParcelable(MainActivity.IMAGE);
            if(bitmap_img != null){
                img.setImageBitmap(bitmap_img);
                img.setBackgroundColor(0);
            }
        }

        username.setText(Html.fromHtml("Hello <b><i>"+MainActivity.user.getUsername()+"</i></b>"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putParcelable(MainActivity.IMAGE,bitmap_img);
    }
}