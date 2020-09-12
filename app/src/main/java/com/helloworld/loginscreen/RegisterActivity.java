package com.helloworld.loginscreen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.helloworld.loginscreen.db.DBAdapter;
import com.helloworld.loginscreen.db.UserAuth;

import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    ImageView img;
    EditText name,mail,pass;
    Button register;
    RadioGroup rg;
    RadioButton male,female;
    CheckBox accept;
    Bitmap bitmap;

    DBAdapter db;
    FileOutputStream fos;

    public static final String IMAGE = "IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        if(savedInstanceState != null) {
            bitmap = (Bitmap) savedInstanceState.getParcelable(IMAGE);
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

//                AlertDialog.Builder alertBuilder;
//                final AlertDialog dialog;
//
//                alertBuilder = new AlertDialog.Builder(RegisterActivity.this);
//                alertBuilder.setCancelable(false);
//                LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
//                alertBuilder.setView(inflater.inflate(R.layout.wait_progress,null));
//
//                dialog = alertBuilder.create();
//
//                final Handler handleToast = new Handler(){
//                    @Override
//                    public void handleMessage(@NonNull Message msg) {
//                        Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
//                    }
//                };
//
//                final Handler startProgressAlert = new Handler() {
//                    @Override
//                    public void handleMessage(@NonNull Message msg) {
//                        dialog.show();
//                    }
//                };
//
//                final Handler endProgressAlert = new Handler() {
//                    @Override
//                    public void handleMessage(@NonNull Message msg) {
//                        dialog.dismiss();
//                    }
//                };
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startProgressAlert.sendEmptyMessage(0);
//
//                        String username,email,password;
//                        username = name.getText().toString();
//                        email = mail.getText().toString();
//                        password = pass.getText().toString();
//                        try {
//                            if(username.isEmpty() || email.isEmpty() || password.isEmpty())
//                                throw new Exception("Fill the empty fields");
//                            /*
//                            int index = email.indexOf("@");
//
//                            if(index == -1)
//                                throw new Exception("Invalid mail");
//
//                            String nameAddress,pathAddress;
//                            nameAddress = email.substring(0,index);
//                            pathAddress = email.substring(index+1);
//
//                            if(nameAddress.isEmpty() || pathAddress.isEmpty())
//                                throw new Exception("Invalid mail");
//
//                            for(int i=0; i<nameAddress.length(); i++)
//                                if(!(nameAddress.charAt(i)>='A' && nameAddress.charAt(i)<='Z'
//                                        || nameAddress.charAt(i)>='a' && nameAddress.charAt(i)<='z'
//                                        || nameAddress.charAt(i)>='0' && nameAddress.charAt(i)<='9'
//                                        || nameAddress.charAt(i)=='_'))
//                                    throw new Exception("Invalid mail");
//
//                            if(pathAddress.charAt(0)=='.'
//                                    || pathAddress.charAt(pathAddress.length()-1)=='.'
//                                    || !pathAddress.contains("."))
//                                throw new Exception("Invalid mail");
//
//                            for(int i=0; i<pathAddress.length()-1; i++)
//                                if (pathAddress.charAt(i)=='.' && pathAddress.charAt(i+1)=='.'
//                                    || !(pathAddress.charAt(i)>='a' && pathAddress.charAt(i)<='z' || pathAddress.charAt(i)=='.'))
//                                    throw new Exception("Invalid mail");
//                            //*/
//
//                            ///*
//                            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
//                                throw new Exception("Invalid mail");
//                            //*/
//                            if(password.length()<6 || password.length()>20)
//                                throw new Exception("The password must have length from 6 to 20");
//
//                            if(!(male.isChecked() || female.isChecked()) || !accept.isChecked())
//                                throw new Exception("The required data isn't completed");
//
//                            if(db.getUser(email)!=null)
//                                throw new Exception("This mail exists, you can't register by it another time");
//
//                            UserAuth user;
//
//                            if (bitmap != null) {
//                                String img_file = System.currentTimeMillis() + "";
//                                fos = openFileOutput(img_file, MODE_PRIVATE);
//                                fos.write(UserAuth.bitmapToByteArray(bitmap));
//                                fos.close();
//                                user = new UserAuth(username, email, password, ((male.isChecked()) ? 'M' : 'F'), img_file);
//                            } else
//                                user = new UserAuth(username, email, password, ((male.isChecked()) ? 'M' : 'F'), null);
//
//                            if (db.addUser(user) <= 0)
//                                throw new Exception("Error happened through registeration, please try again");
//
//                            Message msg = new Message();
//                            msg.obj = "Registeration is done successfully";
//                            handleToast.sendMessage(msg);
//
//                            Intent in = new Intent();
//                            in.putExtra(MainActivity.EMAIL,email);
//                            in.putExtra(MainActivity.PASS,password);
//                            setResult(RESULT_OK,in);
//
//                            finish();
//
//                        }catch (Exception e){
//                            Message msg = new Message();
//                            msg.obj = e.getMessage();
//                            handleToast.sendMessage(msg);
//                        }
//                        endProgressAlert.sendEmptyMessage(0);
//                    }
//                }).start();
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

            //to use only bitmap
            ///*
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
            //*/
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putParcelable(IMAGE,bitmap);
    }

    class WaitProgress extends AsyncTask<Void,Void,Void>{

        AlertDialog.Builder alertBuilder;
        AlertDialog dialog;
        Handler handleToast;

        public WaitProgress(Context context){
            alertBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    alertBuilder.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
                    alertBuilder.setView(inflater.inflate(R.layout.wait_progress,null));

            dialog = alertBuilder.create();

            handleToast = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                }
            };
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String username,email,password;
            username = name.getText().toString();
            email = mail.getText().toString();
            password = pass.getText().toString();
            try {
                if(username.isEmpty() || email.isEmpty() || password.isEmpty())
                    throw new Exception("Fill the empty fields");

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    throw new Exception("Invalid mail");

                if(password.length()<6 || password.length()>20)
                    throw new Exception("The password must have length from 6 to 20");

                if(!(male.isChecked() || female.isChecked()) || !accept.isChecked())
                    throw new Exception("The required data isn't completed");

                if(db.getUser(email)!=null)
                    throw new Exception("This mail exists, you can't register by it another time");

                UserAuth user;

                if (bitmap != null) {
                    String img_file = System.currentTimeMillis() + "";
                    fos = openFileOutput(img_file, MODE_PRIVATE);
                    fos.write(UserAuth.bitmapToByteArray(bitmap));
                    fos.close();
                    user = new UserAuth(username, email, password, ((male.isChecked()) ? 'M' : 'F'), img_file);
                } else
                    user = new UserAuth(username, email, password, ((male.isChecked()) ? 'M' : 'F'), null);

                if (db.addUser(user) <= 0)
                    throw new Exception("Error happened through registeration, please try again");

                Message msg = new Message();
                msg.obj = "Registeration is done successfully";
                handleToast.sendMessage(msg);

                Intent in = new Intent();
                in.putExtra(MainActivity.EMAIL,email);
                in.putExtra(MainActivity.PASS,password);
                setResult(RESULT_OK,in);

                finish();

            }catch (Exception e){
                Message msg = new Message();
                msg.obj = e.getMessage();
                handleToast.sendMessage(msg);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            dialog.dismiss();
            super.onPostExecute(v);
        }
    }

}
