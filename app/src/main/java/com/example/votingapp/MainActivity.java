package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS admin(username VARCHAR, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS poll(pid INTEGER PRIMARY KEY, name VARCHAR, status VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS question(qid INTEGER PRIMARY KEY,  pid INTEGER, que VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS answer(aid INTEGER PRIMARY KEY,  qid INTEGER, ans VARCHAR)");
        db.execSQL("INSERT INTO user VALUES('biserar', '12345');");
        db.execSQL("INSERT INTO admin VALUES('biserarunceva', '12345');");

    }
    public void ClickLogin(View view) {
        Intent intent = null;
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void ClickRegister(View view) {
        Intent intent = null;
        intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

}