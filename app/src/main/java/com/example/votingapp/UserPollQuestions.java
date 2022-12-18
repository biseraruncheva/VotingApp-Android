package com.example.votingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UserPollQuestions extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    int i=0;
    SQLiteDatabase db;
    ArrayList<String> questions = new ArrayList<>();
    //ArrayList<Integer> ansIDs = new ArrayList<Integer>();
    //LinkedHashMap<String,String> IDnQuestion = new LinkedHashMap<String,String>();

    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = true;
    String Pid;
    String Uid;
    Cursor c3;
    TextView timerTextView;
    String checkedRadio;
    String sentQid;
    Random rn = new Random();
    ArrayList<String> PIDs = new ArrayList<String>();
    //ArrayList<String> pids = new ArrayList<>();
    ArrayList<String> pnames = new ArrayList<>();
    String pname;
    String usern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_poll_questions);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        pname = intent.getStringExtra("pollname");
        Pid = intent.getStringExtra("pid");
        Uid = intent.getStringExtra("userid");
        usern = intent.getStringExtra("username");


        TextView timerTextView = (TextView) findViewById(R.id.dispTimer);

        Cursor c1 = db.rawQuery("SELECT * FROM question WHERE  pid = '" +Integer.valueOf(Pid)+"'", null);
        // data to populate the RecyclerView with
        int rows = c1.getCount();
        if (c1.moveToPosition(0)) {
            do {

                questions.add(c1.getString(2));
                //IDnQuestion.put(c1.getString(0),c1.getString(2));
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
            c1.close();
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
        intent.putExtra("qposition", Integer.toString(position+1));
        intent.putExtra("pid", Pid);
        intent.putExtra("userid", Uid);
        intent.putExtra("username",usern );
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
                addNotification(Pid, pname);
                Intent intent = new Intent(UserPollQuestions.this, UserHome.class);
                intent.putExtra("userid", Uid);
                intent.putExtra("pid", Pid);
                intent.putExtra("pollname", pname);
                intent.putExtra("username",usern );
                TextView timerTextView = (TextView) findViewById(R.id.dispTimer);
                timerTextView.setText("Finish!");
                startActivity(intent);
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
                timerTextView.setText("Finish!");


            } else {

                StartTimer();
            }
        }
    }

    public void addNotification(String pid, String pname) {
        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        boolean check;
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            mChannel.setDescription(Description);


            manager.createNotificationChannel(mChannel);
        }

        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM allnotifications, poll WHERE uid = '" + Integer.valueOf(Uid) + "' AND allnotifications.pid = poll.pid ", null);
        int rows = c.getCount();
        int i = 0;
        if (c.moveToFirst()) {
            do {
                PIDs.add(c.getString(4));
                pnames.add(c.getString(6));
                i++;
                c.moveToPosition(i);

            } while (i < rows);
            c.close();

            int k;
            for (k = 0; k < PIDs.size(); k++) {
                end_millis = prefs.getLong("endmillis" + PIDs.get(k), 600000);
                start_millis = prefs.getLong("startmillis" + PIDs.get(k), 6000000);
                total_millis = end_millis - System.currentTimeMillis();

                if (total_millis < 0) {
                    total_millis = 0;
                    TimerRunning = false;
                    db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                    c3 = db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" + Integer.valueOf(PIDs.get(k)) + "'", null);
                    c3.moveToFirst();
                    c3.close();
                    c3 = db.rawQuery("SELECT uid, username FROM user WHERE uid = '" + Integer.valueOf(Uid) + "'", null);
                    c3.moveToFirst();

                    String message = "Hi " + c3.getString(1) + ". The poll " + pnames.get(k) + " voting has ended.";
                    //i++;
                    Cursor c4 = db.rawQuery("UPDATE allnotifications SET context = '" + message + "', notiftype='End' WHERE uid ='" + Integer.valueOf(Uid) + "' AND pid='" + Integer.valueOf(PIDs.get(k)) + "'", null);
                    c4.moveToFirst();
                    c4.close();

                }

            }
        }
        c = db.rawQuery("SELECT * FROM allnotifications WHERE uid = '" + Integer.valueOf(Uid) + "' AND notiftype='End'", null);
        rows = c.getCount();
        i = 0;
        if (c.moveToFirst()) {
            do {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setContentTitle("POLL INFO");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setContentText(c.getString(2));
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                Notification notification = builder.build();
                managerCompat.notify(i, notification);

                i++;
                c.moveToPosition(i);
            } while (i < rows);
            c.close();
            //Cursor cc = db.rawQuery("UPDATE allnotifications SET notiftype='notEnd' WHERE uid = '" + Integer.valueOf(Uid) + "' AND notiftype='Start'", null);
            //cc.moveToFirst();
            //cc.close();
            Cursor cc = db.rawQuery("DELETE FROM allnotifications WHERE uid = '" + Integer.valueOf(Uid) + "' AND notiftype='End'", null);
            cc.moveToFirst();
            cc.close();


        }
    }

    public void GoBackHome(View view) {
        Intent intent = new Intent(this,UserHome.class);
        intent.putExtra("pollname", pname);
        intent.putExtra("pid", Pid);
        intent.putExtra("userid", Uid);
        intent.putExtra("username", usern);

        startActivity(intent);
    }
}





