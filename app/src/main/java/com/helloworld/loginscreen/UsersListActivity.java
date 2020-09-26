package com.helloworld.loginscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.helloworld.loginscreen.db.DBAdapter;
import com.helloworld.loginscreen.db.UserAuth;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TAG";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    AlertDialog.Builder dialog;
    ArrayList<UserAuth>list;
    private DBAdapter db;
    private SharedPreferences sp;
    View headNav;
    CircleImageView prof_img;
    TextView name,mail;
    FragmentManager frag_mgr;
    FragmentTransaction trans;
    private String tag;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        init(savedInstanceState);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        if(savedInstanceState == null) {
            tag = "HOME";
            transactionFragment(new HomeFragment(),tag);
        }else{
            tag = savedInstanceState.getString(TAG);
            Fragment frag;
            if(tag.equals("HOME"))
                frag = new HomeFragment();
            else {
                frag = new InfoFragment();
                navigationView.setCheckedItem(R.id.nav_info);
            }
            transactionFragment(frag,tag);
        }
    }

    private void init(Bundle savedInstanceState) {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nv);
        navigationView.bringToFront();

        headNav = navigationView.getHeaderView(0);
        prof_img = headNav.findViewById(R.id.circuler_profile_img);
        name = headNav.findViewById(R.id.name);
        mail = headNav.findViewById(R.id.mail);

        name.setText(MainActivity.user.getUsername());
        mail.setText(MainActivity.user.getMail());

        if(savedInstanceState == null) {
            if (MainActivity.user.getImage() != null) {
                bitmap = BitmapFactory.decodeByteArray(MainActivity.user.getImage(), 0, MainActivity.user.getImage().length);
                prof_img.setImageBitmap(bitmap);
                prof_img.setBackgroundColor(0);
            } else
                bitmap = null;
        }else{
            bitmap = savedInstanceState.getParcelable(MainActivity.IMAGE);
            if(bitmap != null){
                prof_img.setImageBitmap(bitmap);
                prof_img.setBackgroundColor(0);
            }
        }

        db = new DBAdapter(this);
        sp = getSharedPreferences("Auth", MODE_PRIVATE);
        list = db.getUsers(MainActivity.user.getId());

        frag_mgr = getSupportFragmentManager();
        trans = frag_mgr.beginTransaction();
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                tag = "HOME";
                transactionFragment(new HomeFragment(),tag);
                break;
            case R.id.nav_info:
                tag = "INFO";
                transactionFragment(new InfoFragment(),tag);
                break;
            case R.id.nav_del_acc:
                dialog = createDialog("Delete your account",R.drawable.ic_clear_24,true);
                dialog.create().show();
                break;
            case R.id.nav_logout:
                dialog = createDialog("Logout",R.drawable.ic_exit_24,false);
                dialog.create().show();
                break;
            default:
                return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void transactionFragment(Fragment fragment, String tag){
        Fragment fr = frag_mgr.findFragmentByTag(tag);
        trans = frag_mgr.beginTransaction();
        if(fr == null){
            trans.replace(R.id.frag,fragment,tag);
            trans.commit();
        }else if(fr instanceof HomeFragment && !tag.equals("HOME")
                || fr instanceof InfoFragment && !tag.equals("INFO")){
            trans.replace(R.id.frag,fr,tag);
            trans.commit();
        }
    }

    private AlertDialog.Builder createDialog(String title, int icon, final boolean delete) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Are you sure?")
                .setIcon(icon)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(delete) {
                            if (db.deleteAccount(sp.getInt(MainActivity.USER_ID, -1)) == 0) {
                                Toast.makeText(UsersListActivity.this, "There is a problem throw deleting, please try again", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(MainActivity.USER_ID,-1);
                        editor.commit();
                        MainActivity.user = null;
                        startActivity(new Intent(UsersListActivity.this,MainActivity.class));
                        finish();
                    }
                }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigationView.setCheckedItem(R.id.nav_home);
                    }
                }).setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MainActivity.user.getImage() != null)
            getMenuInflater().inflate(R.menu.setting_menu_if_img_exist,menu);
        else
            getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                prof_img.setImageDrawable(getDrawable(R.drawable.ic_account_circle));
                prof_img.setBackground(getDrawable(R.drawable.white_oval));
                bitmap = null;
                MainActivity.user.setImage(null);
                db.deleteImage(MainActivity.user.getId());
                break;
            case R.id.change_pass:
                startActivity(new Intent(UsersListActivity.this,ChangePasswordActivity.class));
                break;
            case R.id.change_phone:
                startActivity(new Intent(UsersListActivity.this,ChangePasswordActivity.class));
                break;
            default: return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    break;
                case 2:
                    Uri filePath = data.getData();
                    try {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),filePath));
                        else
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            prof_img.setImageBitmap(bitmap);
            prof_img.setBackgroundColor(0);

            MainActivity.user.setImage(DBAdapter.bitmapToByteArray(bitmap));
            db.updateImage(MainActivity.user.getId(),MainActivity.user.getImage());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putString(TAG,tag);
        outState.putParcelable(MainActivity.IMAGE,bitmap);
    }
}