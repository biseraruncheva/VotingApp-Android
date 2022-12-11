package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdminPollDetails extends AppCompatActivity {
    SQLiteDatabase db;
    TextView timerTextView;
    String sdate;
    String edate;
    String stime;
    String etime;
    EditText startDtv;
    EditText endDtv;
    EditText startTtv;
    EditText endTtv;
    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = false;
    Date start_date;
    Date end_date;
    Date current_date;
    String Pid;
    Cursor c1;
    Cursor c2;
    Cursor c3;

    private String EVENT_DATE_TIME = "2023-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_poll_details);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        String pname = intent.getStringExtra("pollname");
        Pid = intent.getStringExtra("pid");
        Integer Npolls = intent.getIntExtra("numpolls", 0);
        TextView tv = (TextView) findViewById(R.id.polldetailsname);
        tv.setText(pname);
        c1 = db.rawQuery("SELECT * FROM question WHERE  pid = '" +Integer.valueOf(Pid)+"'", null);
        int rows = c1.getCount();
        TextView tv1 = (TextView) findViewById(R.id.numq);
        tv1.setText(Integer.toString(rows));

        Button startbtn = findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                timerTextView = findViewById(R.id.timerid);
                startDtv = findViewById(R.id.startD);
                endDtv = findViewById(R.id.endD);
                startTtv = findViewById(R.id.startT);
                endTtv = findViewById(R.id.endT);
                sdate = String.valueOf(startDtv.getText());
                edate = String.valueOf(endDtv.getText());
                stime = String.valueOf(startTtv.getText());
                etime = String.valueOf(endTtv.getText());
                TimerRunning = true;
                start_date = null;
                end_date = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                String pom1 = sdate+" "+stime;
                String pom2 = edate+" "+etime;
                try {
                    start_date = new Date();
                    end_date = (Date) dateFormat.parse(pom2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                start_millis = start_date.getTime(); //get the start time in milliseconds
                end_millis = end_date.getTime(); //get the end time in milliseconds
                total_millis = (end_millis - start_millis); //total time in milliseconds
                if(start_millis <= System.currentTimeMillis()) {
                    db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                    c2=db.rawQuery("UPDATE poll SET status = 'Ongoing' WHERE pid = '" +Integer.valueOf(Pid)+"'",null);
                    c2.moveToFirst();
                    c2.close();
                    StartTimer();
                }
            }
        }) ;
    }

    private void StartTimer(){
        //1000 = 1 second interval
        cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                timerTextView.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));

            }

            @Override
            public void onFinish() {
                timerTextView.setText("Finish!");
            }

        };
        cdt.start();

    };

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft"+Pid, total_millis);
        editor.putLong("startmillis"+Pid, start_millis);
        editor.putLong("endmillis"+Pid, end_millis);
        editor.putString("startd"+Pid, sdate);
        editor.putString("endd"+Pid, edate);
        editor.putString("startt"+Pid, stime);
        editor.putString("endt"+Pid, etime);

        editor.putBoolean("timerrunning"+Pid, TimerRunning);

        editor.apply();

        if (cdt != null) {
            cdt.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);

        total_millis = prefs.getLong("millisLeft"+Pid, 0);
        TimerRunning = prefs.getBoolean("timerrunning"+Pid, false);

        long days = TimeUnit.MILLISECONDS.toDays(total_millis);
        total_millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(total_millis);
        total_millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(total_millis);
        total_millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(total_millis);
        timerTextView = findViewById(R.id.timerid);
        timerTextView.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));

        if (TimerRunning) {
            end_millis = prefs.getLong("endmillis"+Pid, 600000);
            start_millis = prefs.getLong("startmillis"+Pid, 6000000);
            total_millis = end_millis - System.currentTimeMillis();

            if (total_millis < 0) {
                total_millis = 0;
                TimerRunning = false;
                timerTextView.setText("Finish!");
                db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                c3=db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" +Integer.valueOf(Pid)+"'",null);
                c3.moveToFirst();
                c3.close();

            } else {

                StartTimer();
            }
        }
    }




}


