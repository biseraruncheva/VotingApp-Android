package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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
        db.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR UNIQUE, password VARCHAR, uid INTEGER PRIMARY KEY);");
        db.execSQL("CREATE TABLE IF NOT EXISTS admin(username VARCHAR UNIQUE, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS poll(pid INTEGER PRIMARY KEY, name VARCHAR, status VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS question(qid INTEGER PRIMARY KEY,  pid INTEGER, que VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS answer(aid INTEGER PRIMARY KEY,  qid INTEGER, ans VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS myanswer(aid INTEGER ,  qid INTEGER, pid INTEGER, uid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS myinfo(uid INTEGER, pid INTEGER, time INTEGER,  latitude DOUBLE, longitude DOUBLE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS allnotifications(nid INTEGER PRIMARY KEY , uid INTEGER, context VARCHAR, notiftype VARCHAR, pid INTEGER)");
        String s2 = "biserarunceva";
        String s1 = "biserar";
        Cursor c1 = db.rawQuery("SELECT * FROM user WHERE  username = '"+s1+"'", null);
        Cursor c2 = db.rawQuery("SELECT * FROM admin WHERE  username = '"+s2+"'", null);

        if(c1.getCount()>0 && c2.getCount()>0) {
            c1.close();
            c2.close();
        }
        else {
            db.execSQL("INSERT INTO user (username,password) VALUES('biserar', '12345');");
            db.execSQL("INSERT INTO admin (username,password) VALUES('biserarunceva', '12345');");
            c1.close();
            c2.close();
        }

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