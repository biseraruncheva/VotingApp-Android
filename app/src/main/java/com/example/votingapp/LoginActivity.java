package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText usern;
    EditText passw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);

    }

    public void ClLogin(View view) {
        usern = (EditText) findViewById(R.id.loginemail);
        passw = (EditText) findViewById(R.id.loginpassword);

        Cursor c1 = db.rawQuery("SELECT * FROM user WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);
        Cursor c2 = db.rawQuery("SELECT * FROM admin WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);

        if (c1.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, UserHome.class);
            intent.putExtra("username", usern.getText().toString());
            startActivity(intent);
        }
        else if (c2.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, AdminHome.class);
            intent.putExtra("username", usern.getText().toString());
            startActivity(intent);
        }
    }

}