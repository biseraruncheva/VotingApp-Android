package com.example.votingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UserPollQuestions extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    int i=0;
    SQLiteDatabase db;
    ArrayList<String> questions = new ArrayList<>();
    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = true;
    String Pid;
    Cursor c3;
    TextView timerTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll_questions);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        String pname = intent.getStringExtra("pollname");
        Pid = intent.getStringExtra("pid");
        Integer Npolls = intent.getIntExtra("numpolls", 0);
        TextView timerTextView = (TextView) findViewById(R.id.dispTimer);
        StartTimer();

        Cursor c1 = db.rawQuery("SELECT * FROM question WHERE  pid = '" +Integer.valueOf(Pid)+"'", null);
        // data to populate the RecyclerView with
        int rows = c1.getCount();
        if (c1.moveToPosition(0)) {
            do {

                questions.add(c1.getString(2));
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.questionpoll);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, questions);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, UserPollAnswers.class);
        intent.putExtra("question", questions.get(position));
        intent.putExtra("qid", Integer.toString(position+1));
        startActivity(intent);
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
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
                TextView timerTextView = (TextView) findViewById(R.id.dispTimer);


                timerTextView.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));

            }

            @Override
            public void onFinish() {
                TextView timerTextView = (TextView) findViewById(R.id.dispTimer);
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
        //editor.putString("startd"+Pid, sdate);
        //editor.putString("endd"+Pid, edate);
        //editor.putString("startt"+Pid, stime);
        //editor.putString("endt"+Pid, etime);

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
        timerTextView = findViewById(R.id.dispTimer);
        timerTextView.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));

        if (TimerRunning) {
            end_millis = prefs.getLong("endmillis"+Pid, 600000);
            start_millis = prefs.getLong("startmillis"+Pid, 6000000);
            total_millis = end_millis - System.currentTimeMillis();

            if (total_millis < 0) {
                total_millis = 0;
                TimerRunning = false;
                timerTextView.setText("00:00:00:00");
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