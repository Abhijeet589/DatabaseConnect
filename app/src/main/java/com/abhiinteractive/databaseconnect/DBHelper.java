package com.abhiinteractive.databaseconnect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inputdb";
    public static final String TABLE_NAME = "user_input";
    public static final int DATABASE_VERSION = 1;
    public static final int SYNC_SUCCESS = 0;
    public static final int SYNC_FAILED = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create the table
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user_input( " +
                " `input` TEXT NOT NULL ," +
                " `sync` INTEGER NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //Method to save the data to the local database
    public void saveToLocalDatabase(String input, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("input", input);
        contentValues.put("sync", syncStatus);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    //Method to read the data from local database
    public Cursor readFromLocalDatabase(SQLiteDatabase sqLiteDatabase) {
        String columnNames[] = {"input", "sync"};
        return (sqLiteDatabase.query(TABLE_NAME, columnNames, null, null, null, null, null));
    }

    //Method to update the data
    public void updateDatabase(String input, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("input", input);
        contentValues.put("sync", syncStatus);
        String selection = "input" + " LIKE ?";
        String[] selection_args = {input};
        sqLiteDatabase.update(TABLE_NAME, contentValues, selection, selection_args);
    }

    //Method to delete a contact
    public void deleteRow(String name, int syncStatus, SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(TABLE_NAME, "input LIKE ?", new String[]{name});
    }

}
