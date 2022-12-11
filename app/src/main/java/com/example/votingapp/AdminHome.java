package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedHashMap;

public class AdminHome extends AppCompatActivity {
    SQLiteDatabase db;
    int i=0;
    String usern;
    //TextView timerTextView = findViewById(R.id.timerid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Intent intent = getIntent();
        usern = intent.getStringExtra("username");
        TextView text = (TextView) findViewById(R.id.WelcomeID);
        text.setText("WELCOME "+usern);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Cursor c1 = db.rawQuery("SELECT * FROM poll", null);
        int rows = c1.getCount();
        if (c1.moveToPosition(0)) {
            do {
                LinearLayout LL = (LinearLayout) findViewById(R.id.listpolls);
                TextView tv = new TextView(this);
                tv.setText("\n"+c1.getString(0)+". "+c1.getString(1)+" \n\n Status: "+c1.getString(2));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv.setBackgroundResource(R.drawable.my_border);
                //final float scale = AdminHome.this.getApplicationContext().getResources().getDisplayMetrics().density;
                //int pixels = (int) (100 * scale + 0.5f);
                tv.setId(i);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        c1.moveToPosition(view.getId());
                        Intent intent = new Intent(getApplicationContext(), AdminPollDetails.class);
                        intent.putExtra("pollname", c1.getString(1));
                        intent.putExtra("pid", c1.getString(0));
                        intent.putExtra("numpolls", c1.getCount());
                        startActivity(intent);
                    }
                });
                LL.addView(tv);
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
        }
        else
        {
            LinearLayout LL = (LinearLayout) findViewById(R.id.listpolls);
            TextView tv = new TextView(this);
            tv.setText("NO ADDED POLLS YET");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            //tv.setBackgroundResource(R.drawable.my_border);
            //final float scale = AdminHome.this.getApplicationContext().getResources().getDisplayMetrics().density;
            //int pixels = (int) (100 * scale + 0.5f);
           // tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
            LL.addView(tv);
        }


    }
    public void AddPoll(View view) {
        Intent intent = null;
        intent = new Intent(this, AdminAddPoll.class);
        intent.putExtra("username", usern);
        startActivity(intent);

    }



}