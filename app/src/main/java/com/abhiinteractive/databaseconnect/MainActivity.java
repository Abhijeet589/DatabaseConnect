package com.abhiinteractive.databaseconnect;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText inputET;
    String input;
    Button submitBtn;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ArrayList<InputEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputET = findViewById(R.id.input_edit_text);
        submitBtn = findViewById(R.id.submit_input_btn);
        recyclerView = findViewById(R.id.recycler_view);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input = inputET.getText().toString();
                save(input);
            }
        });

        //Setup the recycler view and adapter
        recyclerAdapter = new RecyclerAdapter(this, list);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Refresh the list
        read();

        //On every run, sync the data with the MySQL database
        sync();
    }

    public void save(String input) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        inputET.setText("");

        if (checkForNetwork(this)) {
            AddInputTask addContactTask = new AddInputTask(MainActivity.this);
            addContactTask.execute(input);
            dbHelper.saveToLocalDatabase(input, DBHelper.SYNC_SUCCESS, sqLiteDatabase);
        } else {
            dbHelper.saveToLocalDatabase(input, DBHelper.SYNC_FAILED, sqLiteDatabase);
        }
        read();
    }

    //To read from the SQLite database and populate the recyclerview
    private void read() {
        list.clear();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()) {
            String input = cursor.getString(cursor.getColumnIndex("input"));
            int sync = cursor.getInt(cursor.getColumnIndex("sync"));
            list.add(new InputEntity(input, sync));
        }

        recyclerAdapter.notifyDataSetChanged();
    }

    public void sync(){
        if(checkForNetwork(MainActivity.this)){
            DBHelper dbHelper = new DBHelper(MainActivity.this);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

            Cursor cursor = dbHelper.readFromLocalDatabase(sqLiteDatabase);

            while (cursor.moveToNext()){
                int syncStatus = cursor.getInt(cursor.getColumnIndex("sync"));
                //If the sync was unsuccessful earlier due to no internet, then update the contact now
                if(syncStatus==DBHelper.SYNC_FAILED){
                    String input = cursor.getString(cursor.getColumnIndex("input"));
                    //Execute the task to add contact to the server
                    AddInputTask addContactTask = new AddInputTask(MainActivity.this);
                    addContactTask.execute(input);
                    //Set it's sync to successful now that it's synced
                    dbHelper.updateDatabase(input, DBHelper.SYNC_SUCCESS, sqLiteDatabase);
                }
            }
            dbHelper.close();
        }
    }

    //Static method to check if internet is available or not.
    public static boolean checkForNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
