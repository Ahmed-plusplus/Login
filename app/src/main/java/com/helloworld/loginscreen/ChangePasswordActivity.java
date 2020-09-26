package com.helloworld.loginscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.helloworld.loginscreen.db.DBAdapter;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText curPass,newPass;
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curPassword,newPassword;
                curPassword = curPass.getText().toString();
                newPassword = newPass.getText().toString();
                try {
                    if(curPassword.isEmpty())
                        throw new Exception("Enter your registered phone");
                    if(newPassword.isEmpty())
                        throw new Exception("Enter the new phone");
                    if(newPassword.equals(curPassword))
                        throw new Exception("No change");
                    if(newPassword.length()<6 || newPassword.length()>20)
                        throw new Exception("The password must have length from 6 to 20");
                    DBAdapter db = new DBAdapter(ChangePasswordActivity.this);
                    db.updatePass(MainActivity.user.getId(),newPassword);
                }catch (Exception e){
                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        curPass = findViewById(R.id.cur_pass);
        newPass = findViewById(R.id.new_pass);
        change = findViewById(R.id.change_pass_btn);
    }
}