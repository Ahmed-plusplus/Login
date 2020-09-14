package com.helloworld.loginscreen.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DBAdapter {

    static DBHelper helper;

    public DBAdapter(Context context){
        helper = new DBHelper(context);
    }

    public void onDestroyActivity(){
        helper.close();
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return resizeBitmap(stream.toByteArray());
    }

    private static byte[] resizeBitmap(byte[] img){
        while (img.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.8),(int)(bitmap.getHeight()*0.8),true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            img = stream.toByteArray();
        }
        return img;
    }

    public long addUser(UserAuth user){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_E_MAIL,user.getMail());
        contentValues.put(DBHelper.COL_PASSWORD,user.getPassword());
        contentValues.put(DBHelper.COL_USER_NAME,user.getUsername());
        contentValues.put(DBHelper.COL_PHONE,user.getUsername());
        contentValues.put(DBHelper.COL_GENDER,user.getGender()+"");
        contentValues.put(DBHelper.COL_IMAGE,user.getImage());
        return db.insert(DBHelper.TABLE_NAME,null,contentValues);
    }

    public UserAuth getUser(String mail){
        String []mailSelected = {mail};
        return getUser(DBHelper.COL_E_MAIL + " = ?",mailSelected);
    }

    public UserAuth getUser(int id){
        String []idSelected = {id+""};
        return getUser(DBHelper.COL_UID + " = ?",idSelected);
    }

    private UserAuth getUser(String selected, String[] selectedArgs) {
        UserAuth user = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String []cols = {DBHelper.COL_UID,DBHelper.COL_USER_NAME,DBHelper.COL_PHONE,
                DBHelper.COL_E_MAIL,DBHelper.COL_PASSWORD,
                DBHelper.COL_GENDER,DBHelper.COL_IMAGE};
        @SuppressLint("Recycle")
        Cursor c = db.query(DBHelper.TABLE_NAME,cols,selected,selectedArgs,null,null,null);
        if(c.moveToNext()){
            user = new UserAuth(c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5).charAt(0),
                    c.getBlob(6));
        }
        c.close();
        return user;
    }

    public ArrayList<UserAuth> getUsers(int exceptId){
        ArrayList<UserAuth> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String []cols = {DBHelper.COL_UID,DBHelper.COL_USER_NAME,DBHelper.COL_PHONE,
                DBHelper.COL_E_MAIL,DBHelper.COL_PASSWORD,
                DBHelper.COL_GENDER,DBHelper.COL_IMAGE};
        Cursor c = db.query(DBHelper.TABLE_NAME,cols,null,null,null,null,null);
        while (c.moveToNext()){
            if(exceptId == c.getInt(0))
                continue;
            list.add(new UserAuth(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5).charAt(0),
                    c.getBlob(6)
            ));
        }
        c.close();
        return list;
    }

    public void updateImage(int id, byte[] img){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_IMAGE,img);
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
        public static final String COL_PHONE = "phone";
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
                    + COL_PHONE + " TEXT NOT NULL, "
                    + COL_GENDER + " TEXT NOT NULL, "
                    + COL_IMAGE + " BLOB );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
            onCreate(db);
        }
    }
}
