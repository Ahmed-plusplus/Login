package com.helloworld.loginscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloworld.loginscreen.db.DBAdapter;
import com.helloworld.loginscreen.db.UserAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserDetails extends AppCompatActivity {

    SharedPreferences sp;
    DBAdapter db;
    ImageView img;
    TextView username;
    FileInputStream fin;
    Bitmap bitmap_img;
    FileOutputStream fos;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(savedInstanceState == null){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(UserDetails.this,UsersListActivity.class));
                    finish();
                }
            }
        }).start();

        //registerForContextMenu(img);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_acc:
                if(db.deleteAccount(sp.getInt(MainActivity.USER_ID,-1))==0){
                    Toast.makeText(this, "There is a problem throw deleting, please try again", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.logout:
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(MainActivity.USER_ID,-1);
                editor.commit();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(MainActivity.user.getImage() != null)
            getMenuInflater().inflate(R.menu.img_menu_if_exists,menu);
        else
            getMenuInflater().inflate(R.menu.img_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.take_capture:
                Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intentCam.resolveActivity(getPackageManager())!=null)
                    startActivityForResult(intentCam,1);
                break;
            case R.id.choose_img:
                Intent intentImg = new Intent();
                intentImg.setType("image/*");
                intentImg.setAction(Intent.ACTION_PICK);
                startActivityForResult(intentImg,2);
                break;
            case R.id.remove_img:
                img.setImageDrawable(getDrawable(R.drawable.ic_baseline_account_box));
                img.setBackgroundColor(getResources().getColor(android.R.color.white));
                MainActivity.user.setImage(null);
                db.deleteImage(MainActivity.user.getId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    bitmap_img = (Bitmap) data.getExtras().get("data");
                    break;
                case 2:
                    Uri filePath = data.getData();
                    try {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                            bitmap_img = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),filePath));
                        else
                            bitmap_img = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            img.setImageBitmap(bitmap_img);
            img.setBackgroundColor(0);

            MainActivity.user.setImage(DBAdapter.bitmapToByteArray(bitmap_img));
            db.updateImage(MainActivity.user.getId(),MainActivity.user.getImage());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putParcelable(MainActivity.IMAGE,bitmap_img);
    }
}