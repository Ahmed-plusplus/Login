package com.helloworld.loginscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.helloworld.loginscreen.db.DBAdapter;

public class ChangePhoneActivity extends AppCompatActivity {

    EditText curPhone,newPhone;
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        init();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curPh,newPh;
                curPh = curPhone.getText().toString();
                newPh = newPhone.getText().toString();
                try {
                    if(curPh.isEmpty())
                        throw new Exception("Enter your registered phone");
                    if(newPh.isEmpty())
                        throw new Exception("Enter the new phone");
                    if(newPh.equals(curPh))
                        throw new Exception("No change");
                    DBAdapter db = new DBAdapter(ChangePhoneActivity.this);
                    db.updatePhone(MainActivity.user.getId(),newPh);
                }catch (Exception e){
                    Toast.makeText(ChangePhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        curPhone = findViewById(R.id.cur_phone);
        newPhone = findViewById(R.id.new_phone);
        change = findViewById(R.id.change_phone_btn);
    }
}