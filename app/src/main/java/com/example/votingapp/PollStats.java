package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PollStats extends AppCompatActivity {
    SQLiteDatabase db;
    int i = 0, j = 0, k = 0;
    int sizei=0, sizej=0, sizek=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_stats);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        // String pname = intent.getStringExtra("pollname");
        String Pid = intent.getStringExtra("pid");
        //LinearLayout LL = findViewById(R.id.LLstats);
        //TextView tv = new TextView(this);
        //TextView tv1 = new TextView(this);
        //TextView tv2 = new TextView(this);

        Cursor c = db.rawQuery("SELECT * FROM question WHERE pid = '" + Integer.valueOf(Pid) + "' ", null);
        sizei=c.getCount();
        i=0;
        if (c.moveToPosition(0)) {
            do {
                LinearLayout LL = findViewById(R.id.LLstats);
                TextView tv = new TextView(this);
                tv.setText("Prasanje: "+c.getString(2));
                LL.addView(tv);
                Cursor c1 = db.rawQuery("SELECT * FROM answer WHERE qid = '" + Integer.valueOf(c.getString(0)) + "' ", null);
                sizej=c1.getCount();
                i++;
                j=0;
                if (c1.moveToPosition(0)) {
                    do {
                        Cursor c2 = db.rawQuery("SELECT * FROM myanswer WHERE aid = '" + Integer.valueOf(c1.getString(0)) + "' ", null);
                        sizek=c2.getCount();
                        j++;
                        ///k=0;
                       if (sizek >0) {
                            if (c2.moveToPosition(0)) {
                               // do {
                                    LinearLayout LL1 = findViewById(R.id.LLstats);
                                    TextView tv2 = new TextView(this);

                                        Cursor c3 = db.rawQuery("SELECT COUNT(uid) FROM myanswer WHERE aid = '" + Integer.valueOf(c2.getString(0)) + "' ", null);
                                        c3.moveToFirst();
                                        tv2.setText("Na odgovorot " + c1.getString(2) + " glasale " + c3.getString(0));
                                        LL1.addView(tv2);
                                    //LL.addView(tv2);
                                    k++;
                                    c3.close();


                                   // c2.moveToPosition(k);
                               // } while (k < sizek);

                            }

                        }
                        else {
                            LinearLayout LL1 = findViewById(R.id.LLstats);
                            TextView tv2 = new TextView(this);
                            tv2.setText("Na odgovorot " + c1.getString(2) + " glasale 0");
                            LL1.addView(tv2);
                        }
                        c2.close();

                        c1.moveToPosition(j);
                    } while (j<sizej);
                    c1.close();

                }
                //c1 = db.rawQuery("SELECT (COUNT(DISTINCT uid)) FROM myanswer WHERE pid = '" +Integer.valueOf(Pid)+"'",null);

                c.moveToPosition(i);
            }while (i<sizei);
            c.close();
        }
    }
}