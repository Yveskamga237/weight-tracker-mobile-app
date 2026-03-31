package com.example.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDB extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "users.db";
    private static UserDB _UserDB;

    //make the constructor private since this will be a singleton
    private UserDB(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    //singleton
    public static UserDB getInstance(Context context) {
        if (_UserDB == null) {
            _UserDB = new UserDB(context);
        }
        return _UserDB;
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        _db.execSQL("create Table users(username TEXT primary key, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
        _db.execSQL("drop Table if exists users");
    }

    //insert a new user
    public Boolean insertUser(String userName, String password){
        SQLiteDatabase _db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("username", userName);
        values.put("password", password);
        long writeResult = _db.insert("users", null, values);

        //check if the write result is successful
        return (writeResult == -1) ? false : true;
    }

    //check the username
    public Boolean checkUserName(String userName){
        SQLiteDatabase _db = this.getWritableDatabase();
        Cursor cursor = _db.rawQuery("Select * from users where username = ?",
                new String[]{userName});
        return (cursor.getCount() > 0) ? true : false;
    }

    //if username doesn't exist create it with password

    //check the password
    public Boolean checkUserPassword(String userName, String password){
        SQLiteDatabase _db = this.getWritableDatabase();
        Cursor cursor = _db.rawQuery("Select * from users where username = ? and password =?",
                new String[]{userName, password});
        return (cursor.getCount() > 0) ? true : false;
    }

    //delete all users
    public void deleteUser(UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();
        _db.delete("users", "username = ?", new String[]{_user.getUserName()} );
    }
}
