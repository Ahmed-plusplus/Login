package com.helloworld.loginscreen.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    static DBHelper helper;

    public DBAdapter(Context context){
        if(helper != null)
            helper.close();
        helper = new DBHelper(context);
    }

    public long addUser(UserAuth user){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_E_MAIL,user.getMail());
        contentValues.put(DBHelper.COL_PASSWORD,user.getPassword());
        contentValues.put(DBHelper.COL_USER_NAME,user.getUsername());
        contentValues.put(DBHelper.COL_GENDER,user.getGender()+"");
        contentValues.put(DBHelper.COL_IMAGE,user.getImage_file());
        return db.insert(DBHelper.TABLE_NAME,null,contentValues);
    }

    public UserAuth getUser(String mail){
        UserAuth user = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String []cols = {DBHelper.COL_UID,DBHelper.COL_USER_NAME,
                DBHelper.COL_E_MAIL,DBHelper.COL_PASSWORD,
                DBHelper.COL_GENDER,DBHelper.COL_IMAGE};
        String []mailSelected = {mail};
        @SuppressLint("Recycle")
        Cursor c = db.query(DBHelper.TABLE_NAME,cols,DBHelper.COL_E_MAIL + " = ?",mailSelected,null,null,null);
        if(c.moveToNext()){
            user = new UserAuth(c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4).charAt(0),
                    c.getString(5));
        }
        c.close();
        return user;
    }

    public UserAuth getUser(int id){
        UserAuth user = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String []cols = {DBHelper.COL_UID,DBHelper.COL_USER_NAME,
                DBHelper.COL_E_MAIL,DBHelper.COL_PASSWORD,
                DBHelper.COL_GENDER,DBHelper.COL_IMAGE};
        String []idSelected = {id+""};
        @SuppressLint("Recycle")
        Cursor c = db.query(DBHelper.TABLE_NAME,cols,DBHelper.COL_UID + " = ?",idSelected,null,null,null);
        if(c.moveToNext()){
            user = new UserAuth(c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4).charAt(0),
                    c.getString(5));
        }
        c.close();
        return user;
    }

    public void updateImage(int id, String img_file){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_IMAGE,img_file);
        String []idSelected = {id+""};
        db.update(DBHelper.TABLE_NAME, contentValues, DBHelper.COL_UID + " LIKE ?", idSelected);
    }

    public void deleteImage(int id){
        updateImage(id,null);
    }

    public int deleteAccount(int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String []idSelected = {id+""};
        return db.delete(DBHelper.TABLE_NAME,DBHelper.COL_UID + " LIKE ?",idSelected);
    }

    static class DBHelper extends SQLiteOpenHelper{

        public static final int VERSION = 1;
        public static final String DB_NAME = "Auth.db";
        public static final String TABLE_NAME = "USER_AUTH";
        public static final String COL_UID = "_id";
        public static final String COL_E_MAIL = "mail";
        public static final String COL_PASSWORD = "password";
        public static final String COL_USER_NAME = "name";
        public static final String COL_GENDER = "gender";
        public static final String COL_IMAGE = "image";


        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " ( "
                    + COL_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_E_MAIL + " TEXT UNIQUE NOT NULL, "
                    + COL_PASSWORD + " TEXT NOT NULL, "
                    + COL_USER_NAME + " TEXT NOT NULL, "
                    + COL_GENDER + " TEXT NOT NULL, "
                    + COL_IMAGE + " TEXT );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
            onCreate(db);
        }
    }
}
