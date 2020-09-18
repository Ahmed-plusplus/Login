package com.helloworld.loginscreen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoFragment extends Fragment {

    CircleImageView img;
    TextView name,mail,phone,gender;
    private Bitmap bitmap;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        img = view.findViewById(R.id.info_img);
        name = view.findViewById(R.id.info_name);
        mail = view.findViewById(R.id.info_mail);
        phone = view.findViewById(R.id.info_phone);
        gender = view.findViewById(R.id.info_gender);

        name.setText(MainActivity.user.getUsername());
        mail.setText(MainActivity.user.getMail());
        phone.setText(MainActivity.user.getPhone());
        gender.setText((MainActivity.user.getGender()=='M')?"Male":"Female");
        if(savedInstanceState == null) {
            if (MainActivity.user.getImage() != null) {
                bitmap = BitmapFactory.decodeByteArray(MainActivity.user.getImage(), 0, MainActivity.user.getImage().length);
                img.setImageBitmap(bitmap);
                img.setBackgroundColor(0);
            } else
                bitmap = null;
        }else{
            bitmap = savedInstanceState.getParcelable(MainActivity.IMAGE);
            if(bitmap != null){
                img.setImageBitmap(bitmap);
                img.setBackgroundColor(0);
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putParcelable(MainActivity.IMAGE,bitmap);
    }
}