package com.helloworld.loginscreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.helloworld.loginscreen.db.DBAdapter;
import com.helloworld.loginscreen.db.UserAuth;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    ImageView img;
    EditText name,phone,mail,pass;
    Button register;
    RadioGroup rg;
    RadioButton male,female;
    CheckBox accept;
    Bitmap bitmap;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        if(savedInstanceState != null) {
            bitmap = (Bitmap) savedInstanceState.getParcelable(MainActivity.IMAGE);
            if(bitmap!=null) {
                img.setImageBitmap(bitmap);
                img.setBackgroundColor(0);
            }
        }

        registerForContextMenu(img);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WaitProgress(RegisterActivity.this).execute();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Press on the picture to change it", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        img = findViewById(R.id.profile_img);
        name = findViewById(R.id.register_username);
        phone = findViewById(R.id.register_phone);
        mail = findViewById(R.id.register_mail);
        pass = findViewById(R.id.register_pass);
        register = findViewById(R.id.register);
        rg = findViewById(R.id.rg);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        accept = findViewById(R.id.accepted);

        db = new DBAdapter(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(bitmap != null)
            getMenuInflater().inflate(R.menu.img_menu_if_exists,menu);
        else
            getMenuInflater().inflate(R.menu.img_menu,menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
                img.setImageDrawable(getDrawable(R.drawable.ic_add_photo));
                img.setBackgroundColor(getResources().getColor(android.R.color.white));
                bitmap = null;
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getExtras()!=null){
            bitmap = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bitmap);
            img.setBackgroundColor(0);
        }else if(requestCode == 2 && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            Uri filePath = data.getData();
            //img.setImageURI(filePath);

            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),filePath));
                else
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                img.setImageBitmap(bitmap);
                img.setBackgroundColor(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putParcelable(MainActivity.IMAGE,bitmap);
    }

    class WaitProgress extends AsyncTask<Void,Void,String>{

        AlertDialog.Builder alertBuilder;
        AlertDialog dialog;

        public WaitProgress(Context context){
            alertBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    alertBuilder.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
                    alertBuilder.setView(inflater.inflate(R.layout.wait_progress,null));

            dialog = alertBuilder.create();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String username,phone_num,email,password;
            username = name.getText().toString();
            phone_num = phone.getText().toString();
            email = mail.getText().toString();
            password = pass.getText().toString();

            if(username.isEmpty() || email.isEmpty() || password.isEmpty() || phone_num.isEmpty())
                return "Fill the empty fields";

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return "Invalid mail";

            if(password.length()<6 || password.length()>20)
                return "The password must have length from 6 to 20";

            if(!(male.isChecked() || female.isChecked()) || !accept.isChecked())
                return "The required data isn't completed";

            UserAuth user;

            if (bitmap != null) {
                user = new UserAuth(username, phone_num, email, password, ((male.isChecked()) ? 'M' : 'F'), DBAdapter.bitmapToByteArray(bitmap));
            } else
                user = new UserAuth(username, phone_num, email, password, ((male.isChecked()) ? 'M' : 'F'), null);

            if (db.addUser(user) <= 0){
                if(db.getUser(email)!=null)
                    return "This mail exists, you can't register by it another time";
                else
                    return "This phone is used before";
            }

            Intent in = new Intent();
            in.putExtra(MainActivity.EMAIL,email);
            in.putExtra(MainActivity.PASS,password);
            setResult(RESULT_OK,in);

            finish();

            return "Registeration is done successfully";
        }


        @Override
        protected void onPostExecute(String msg) {
            dialog.dismiss();
            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
            super.onPostExecute(msg);
        }
    }

    @Override
    protected void onDestroy() {
        db.onDestroyActivity();
        super.onDestroy();
    }
}
