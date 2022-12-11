package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText usern;
    EditText passw;
    EditText confirmpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);

    }

    public void ClRegister(View view) {

        usern = (EditText) findViewById(R.id.regusername);
        passw = (EditText) findViewById(R.id.regpassword);
        confirmpass = (EditText) findViewById(R.id.regpassword2);

        if(passw.getText().toString().equals(confirmpass.getText().toString())) {

            db.execSQL("INSERT INTO user VALUES('" + usern.getText().toString() + "','"+passw.getText().toString()+"' );");
            Intent intent = null;
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }
}