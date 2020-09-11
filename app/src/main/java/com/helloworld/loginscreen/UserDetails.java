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
    UserAuth user;
    ImageView img;
    TextView username,gender;
    int id;
    String name,gen;
    char g;
    FileInputStream fin;
    Bitmap bitmap_img;
    String image_file;
    FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init(savedInstanceState);

        registerForContextMenu(img);
    }

    private void init(Bundle savedInstanceState) {
        db = new DBAdapter(this);
        sp = getSharedPreferences("Auth",MODE_PRIVATE);
        img = findViewById(R.id.user_img);
        username = findViewById(R.id.hello_user);
        gender = findViewById(R.id.user_gender);
        if(savedInstanceState == null) {
            Intent intent = getIntent();
            id = intent.getExtras().getInt(MainActivity.USER_ID);
            name = intent.getExtras().getString(MainActivity.USERNAME);
            g = intent.getExtras().getChar(MainActivity.GENDER);
            if (g == 'M')
                gen = "Male";
            else
                gen = "Female";
            image_file = intent.getExtras().getString(MainActivity.IMAGE);
            if (image_file != null) {
                try {
                    fin = openFileInput(image_file);
                    byte[] image = new byte[fin.available()];
                    fin.read(image);
                    bitmap_img = BitmapFactory.decodeByteArray(image, 0, image.length);
                    img.setImageBitmap(bitmap_img);
                    img.setBackgroundColor(0);
                    fin.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else
                bitmap_img = null;
        }else{
            id = savedInstanceState.getInt(MainActivity.USER_ID);
            name = savedInstanceState.getString(MainActivity.USERNAME);
            gen = savedInstanceState.getString(MainActivity.GENDER);
            bitmap_img = savedInstanceState.getParcelable(MainActivity.IMAGE);
            if(bitmap_img != null){
                img.setImageBitmap(bitmap_img);
                img.setBackgroundColor(0);
            }
        }
        user = new UserAuth(id,name,null,null,(gen.equals("Male"))?'M':'F',image_file);

        username.setText(Html.fromHtml("Hello <b><i>"+name+"</i></b>"));
        gender.setText(Html.fromHtml("Gender: <span style=\"color:red\">"+gen+"</span>"));
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
        if(user.getImage_file() != null)
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
                user.setImage_file(null);
                db.deleteImage(user.getId());
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
            String img_file = System.currentTimeMillis() + "";
            try {
                fos = openFileOutput(img_file, MODE_PRIVATE);
                fos.write(UserAuth.bitmapToByteArray(bitmap_img));
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setImage_file(image_file);
            db.updateImage(user.getId(),user.getImage_file());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putInt(MainActivity.USER_ID,id);
        outState.putString(MainActivity.USERNAME,name);
        outState.putString(MainActivity.GENDER,gen);
        outState.putParcelable(MainActivity.IMAGE,bitmap_img);
    }
}