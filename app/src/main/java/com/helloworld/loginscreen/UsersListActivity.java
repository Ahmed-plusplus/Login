package com.helloworld.loginscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.helloworld.loginscreen.db.DBAdapter;

import java.lang.reflect.Array;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    AlertDialog.Builder dialog;
    RecyclerView recyclerView;
    private DBAdapter db;
    private SharedPreferences sp;
    View headNav;
    CircleImageView prof_img;
    TextView name,mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        init();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void init() {
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

        if (MainActivity.user.getImage() != null) {
            prof_img.setImageBitmap(BitmapFactory.decodeByteArray(MainActivity.user.getImage(), 0, MainActivity.user.getImage().length));
            prof_img.setBackgroundColor(0);
        }

        db = new DBAdapter(this);
        sp = getSharedPreferences("Auth", MODE_PRIVATE);
        recyclerView = new RecyclerView(this);
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
                recyclerView.setScrollY(0);
                break;
            case R.id.nav_info:
                startActivity(new Intent(UsersListActivity.this,InfoActivity.class));
                navigationView.setCheckedItem(R.id.nav_home);
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
}