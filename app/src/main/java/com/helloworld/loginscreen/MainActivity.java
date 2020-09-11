package com.helloworld.loginscreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.helloworld.loginscreen.db.DBAdapter;
import com.helloworld.loginscreen.db.UserAuth;

public class MainActivity extends AppCompatActivity {

    EditText mail,pass;
    Button in,up;
    TextView forget;
    DBAdapter db;
    UserAuth user;
    SharedPreferences sp;

    public static final String USER_ID = "ID";
    public static final String USERNAME = "Username";
    public static final String EMAIL = "Email";
    public static final String PASS = "Pass";
    public static final String GENDER = "Gender";
    public static final String IMAGE = "Image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(sp.getInt(USER_ID,-1)!=-1){
            Intent intent = new Intent(this,UserDetails.class);
            user = db.getUser(sp.getInt(USER_ID,-1));
            intent.putExtra(USER_ID,user.getId());
            intent.putExtra(USERNAME,user.getUsername());
            intent.putExtra(GENDER,user.getGender());
            intent.putExtra(IMAGE,user.getImage_file());
            startActivity(intent);
            finish();
        }

        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = pass.getText().toString();

                if(email.isEmpty())
                    Toast.makeText(MainActivity.this, getString(R.string.enter_mail), Toast.LENGTH_SHORT).show();
                else if(password.isEmpty())
                    Toast.makeText(MainActivity.this, getString(R.string.enter_pass), Toast.LENGTH_SHORT).show();
                else if((user = db.getUser(email)) != null){
                    if(user.getPassword().equals(password)) {
                        Toast.makeText(MainActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(USER_ID, user.getId());
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, UserDetails.class);
                        intent.putExtra(USER_ID, user.getId());
                        intent.putExtra(USERNAME, user.getUsername());
                        intent.putExtra(GENDER, user.getGender());
                        intent.putExtra(IMAGE, user.getImage_file());
                        startActivity(intent);
                        finish();
                    }else
                        Toast.makeText(MainActivity.this, getString(R.string.pass_not_correct), Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(MainActivity.this, getString(R.string.mail_not_exist), Toast.LENGTH_SHORT).show();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0);
            }
        });

    }

    private void init() {
        mail = findViewById(R.id.login_mail);
        pass = findViewById(R.id.login_pass);
        in = findViewById(R.id.signin);
        up = findViewById(R.id.signup);
        forget = findViewById(R.id.forget_pass);
        db = new DBAdapter(this);
        sp = getSharedPreferences("Auth",MODE_PRIVATE);

        forget.setText(Html.fromHtml("<a href=\"#\">"+getString(R.string.forget_password)+"</a>"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK && data!=null && data.getExtras()!=null){
            mail.setText(data.getExtras().getString(EMAIL));
            pass.setText(data.getExtras().getString(PASS));
        }
    }
}
