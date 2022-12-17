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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdminPollDetails extends AppCompatActivity {
    SQLiteDatabase db;
    TextView timerTextView;
    String pname;
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
        pname = intent.getStringExtra("pollname");
        Pid = intent.getStringExtra("pid");
        Integer Npolls = intent.getIntExtra("numpolls", 0);
        TextView tv = (TextView) findViewById(R.id.polldetailsname);
        tv.setText(pname);
        c1 = db.rawQuery("SELECT * FROM question WHERE  pid = '" +Integer.valueOf(Pid)+"'", null);
        int rows = c1.getCount();
        TextView tv1 = (TextView) findViewById(R.id.numq);
        tv1.setText(Integer.toString(rows));
        c1.close();
        c1 = db.rawQuery("SELECT (COUNT(DISTINCT uid)) FROM myanswer WHERE pid = '" +Integer.valueOf(Pid)+"'",null);
        c1.moveToFirst();
        TextView tv2 = (TextView) findViewById(R.id.numvoters);
        tv2.setText(c1.getString(0));
        c1.close();


        Button startbtn = findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                timerTextView = findViewById(R.id.timerid);
                //startDtv = findViewById(R.id.startD);
                endDtv = findViewById(R.id.endD);
                //startTtv = findViewById(R.id.startT);
                endTtv = findViewById(R.id.endT);
                //sdate = String.valueOf(startDtv.getText());
                edate = String.valueOf(endDtv.getText());
                //stime = String.valueOf(startTtv.getText());
                etime = String.valueOf(endTtv.getText());
                TimerRunning = true;
                start_date = null;
                end_date = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                //String pom1 = sdate+" "+stime;
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
                    c2=db.rawQuery("SELECT uid, username FROM user",null);
                    int rows = c2.getCount();
                    int i=0;
                    if(c2.moveToFirst()){
                        do{
                            String message = "Hi "+c2.getString(1)+". The poll "+pname+" is now available.";
                            db.execSQL("INSERT INTO allnotifications (uid, context, notiftype, pid) VALUES('"+Integer.valueOf(c2.getString(0))+"','"+message+"','Start','"+Integer.valueOf(Pid)+"' );");
                            i++;
                            c2.moveToPosition(i);
                        }while(i<rows);
                        c2.close();
                    }
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
                //db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                //c2=db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" +Integer.valueOf(Pid)+"'",null);
                //c2.moveToFirst();
               // c2.close();
                timerTextView.setText("Finish1!");
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
        //editor.putString("startd"+Pid, sdate);
        editor.putString("endd"+Pid, edate);
        //editor.putString("startt"+Pid, stime);
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
                timerTextView.setText("Finish2!");
                db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                c3=db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" +Integer.valueOf(Pid)+"'",null);
                c3.moveToFirst();
                c3.close();
                c3=db.rawQuery("SELECT uid, username FROM user",null);
                int rows = c3.getCount();
                int i=0;
                if(c3.moveToFirst()){
                    do{
                        String message = "Hi "+c3.getString(1)+". The poll "+pname+" voting has ended.";
                        //db.execSQL("INSERT INTO allnotifications (uid, context, notiftype, pid) VALUES('"+Integer.valueOf(c3.getString(0))+"','"+message+"','End','"+Integer.valueOf(Pid)+"');");
                        i++;
                        //Cursor c4 = db.rawQuery("DELETE FROM allnotifications WHERE uid ='"+Integer.valueOf(c3.getString(0))+"' AND pid='"+Integer.valueOf(Pid)+"'AND notiftype='Start' ",null);
                        Cursor c4 = db.rawQuery("UPDATE allnotifications SET context = '"+message+"', notiftype='End' WHERE uid ='"+Integer.valueOf(c3.getString(0))+"' AND pid='"+Integer.valueOf(Pid)+"'",null);
                        c4.moveToFirst();
                        c4.close();
                        c3.moveToPosition(i);
                    }while(i<rows);
                    c3.close();
                }

            } else {

                StartTimer();
            }
        }
    }


    public void ShowStats(View view) {
        Intent intent = null;
        intent = new Intent(this, PollStats.class);
        intent.putExtra("pid", Pid);
        startActivity(intent);
    }

    public void ShowMap(View view) {

        Intent intent = null;
        intent = new Intent(this, MapsActivity.class);
        intent.putExtra("pid", Pid);
        startActivity(intent);
    }
}


