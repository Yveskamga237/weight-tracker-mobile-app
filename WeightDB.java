package com.example.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeightDB extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weights.db";
    private static WeightDB _weightDB;

    //make the constructor private since this will be a singleton
    private WeightDB(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    //singleton
    public static WeightDB getInstance(Context context) {
        if (_weightDB == null) {
            _weightDB = new WeightDB(context);
        }
        return _weightDB;
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        //create the user's weight diary
        _db.execSQL("create Table weights(_ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, date text, weight float)");

        //create the goals per user
        _db.execSQL("create Table goals(username TEXT primary key, goal float)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
        _db.execSQL("drop Table if exists weights");
        _db.execSQL("drop Table if exists goals");
    }

    public Boolean addEntry(WeightsClass _entry, UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put("username", _user.getUserName());
        values.put("date", _entry.getDate());
        values.put("weight", _entry.getWeight());

        long id = _db.insert("weights", null, values);
        return id != -1;
    }

    public void removeEntry(Integer entryID){
        SQLiteDatabase _db = this.getWritableDatabase();
        _db.delete("weights","_id = ?",new String[]{String.valueOf(entryID)});
    }

    public Boolean updateEntry(int _id, float weight, UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put("username", _user.getUserName());
        values.put("weight", weight);

        long id = _db.update("weights", values, "_id = " + _id, null);
        return id != -1;
    }


    public void addGoal(UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        values.put("username", _user.getUserName());
        values.put("goal", _user.getGoal());

        //check if user has a goal
        Boolean goalSet = false;
        long id = 0; //error checking later

        String Query = "Select * from goals";
        Cursor cursor = _db.rawQuery(Query, null);

        if(cursor.moveToFirst()){
            do {
                //get the username from the db entry
                String username = cursor.getString(0);
                if(username.equals(_user.getUserName())){
                    goalSet = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        if(!goalSet){
            id = _db.insert("goals", null, values);
        }
        else{
            id = _db.updateWithOnConflict("goals", values, "username = ?",
                    new String[] {_user.getUserName()}, SQLiteDatabase.CONFLICT_REPLACE );
        }
    }

    public float getGoal(UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();
        ContentValues values= new ContentValues();

        //check if user has a goal
        float goalSet = 0;

        String Query = "Select * from goals";
        Cursor cursor = _db.rawQuery(Query, null);

        if(cursor.moveToFirst()){
            do {
                //get the username from the db entry
                String username = cursor.getString(0);
                if(username.equals(_user.getUserName())){
                    goalSet = cursor.getFloat(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        return (goalSet != 0) ? goalSet : 0;

    }

    public List<WeightsClass> getAllWeights(UserModel _user) throws ParseException {

        List<WeightsClass> allEntry = new ArrayList<>();
        SQLiteDatabase _db = this.getWritableDatabase();

        Cursor cursor = _db.rawQuery("SELECT * FROM weights ORDER BY date", null);

        if(cursor.moveToFirst()){
            do {
                //get the username from the db entry
                String username = cursor.getString(1);

                //if the username matches the user logged in then start the loop
                if (username.equals(_user.getUserName())){

                    int ID = cursor.getInt(0);
                    String date = cursor.getString(2);

                    //change the date from ISO to mm-dd-yyyy
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date newDate = null;
                    String prettyDate = null;
                    try {
                        newDate = format.parse(date);
                        format = new SimpleDateFormat("MM-dd-YYYY");
                        prettyDate = format.format(newDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int userWeight = cursor.getInt(3);

                    //use the constructor to make objects
                    //add the objects to the list
                    WeightsClass newEntry = new WeightsClass(ID, prettyDate, userWeight);
                    allEntry.add(newEntry);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allEntry;
    }

    public void deleteUser(UserModel _user){
        SQLiteDatabase _db = this.getWritableDatabase();

        _db.delete("goals", "username = ?", new String[]{_user.getUserName()} );
        _db.delete("weights", "username = ?", new String[]{_user.getUserName()} );
    }
}