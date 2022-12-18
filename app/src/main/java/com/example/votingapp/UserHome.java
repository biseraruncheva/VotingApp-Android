package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UserHome extends AppCompatActivity {
    SQLiteDatabase db;
    int i=0;
    String Uid;
    ArrayList<String> PIDs = new ArrayList<String>();
    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = true;
    //ArrayList<String> pids = new ArrayList<>();
    ArrayList<String> pnames = new ArrayList<>();
    ArrayList<CountDownTimer> cdts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent intent = getIntent();
        String usern = intent.getStringExtra("username");
        Uid = intent.getStringExtra("userid");
        TextView text = (TextView) findViewById(R.id.WelcomeUserID);
        text.setText("WELCOME "+usern);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Cursor c1 = db.rawQuery("SELECT * FROM poll WHERE status='Ongoing'", null);
        int rows = c1.getCount();
        if (c1.moveToPosition(0)) {
            do {
                LinearLayout LL = (LinearLayout) findViewById(R.id.listactivepolls);
                TextView tv = new TextView(this);
                PIDs.add(c1.getString(0));
                pnames.add(c1.getString(1));
                //StartTimer(c1.getString(0), i);
                tv.setText("\n"+c1.getString(0)+". "+c1.getString(1)+" \n\n Status: "+c1.getString(2));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv.setBackgroundResource(R.drawable.my_border);
                final float scale = UserHome.this.getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
                tv.setId(i);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //c1.moveToPosition(view.getId());
                        if(addNotification(PIDs.get(view.getId()), pnames.get(view.getId()))){
                            finish();
                            startActivity(getIntent());

                        }else
                        {
                            Intent intent = new Intent(getApplicationContext(), UserPollQuestions.class);
                            intent.putExtra("pollname", pnames.get(view.getId()));
                            intent.putExtra("pid", PIDs.get(view.getId()));
                            intent.putExtra("userid", Uid);
                            intent.putExtra("username",usern );
                            // intent.putExtra("numpolls", c1.getCount());
                            startActivity(intent);
                        }
                    }
                });
                LL.addView(tv);
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
            c1.close();
        }
        else
        {
            LinearLayout LL = (LinearLayout) findViewById(R.id.listactivepolls);
            TextView tv = new TextView(this);
            tv.setText("NO ADDED POLLS YET");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            LL.addView(tv);
            c1.close();
        }


    }

    public void ShowMyPolls(View view) {

        Intent intent = new Intent(this, UserMyPolls.class);
        intent.putExtra("userid", Uid);
        //intent.putExtra("checkedradio",checkedRadio);
        startActivity(intent);
    }
    public boolean addNotification(String pid, String pname) {
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

                end_millis = prefs.getLong("endmillis" + pid, 600000);
                start_millis = prefs.getLong("startmillis" + pid, 6000000);
                total_millis = end_millis - System.currentTimeMillis();

                if (total_millis < 0) {
                    total_millis = 0;
                    TimerRunning = false;
                    db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                    Cursor c3 = db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" + Integer.valueOf(pid) + "'", null);
                    c3.moveToFirst();
                    c3.close();
                    c3 = db.rawQuery("SELECT uid, username FROM user WHERE uid = '"+Integer.valueOf(Uid)+"'", null);
                    c3.moveToFirst();

                    String message = "Hi "+c3.getString(1)+". The poll "+pname+" voting has ended.";
                    //i++;
                    Cursor c4 = db.rawQuery("UPDATE allnotifications SET context = '"+message+"', notiftype='End' WHERE uid ='"+Integer.valueOf(Uid)+"' AND pid='"+Integer.valueOf(pid)+"'",null);
                    c4.moveToFirst();
                    c4.close();
                    check = true;
                    Cursor c = db.rawQuery("SELECT * FROM allnotifications, poll WHERE uid = '"+Integer.valueOf(Uid)+"' AND allnotifications.pid = poll.pid ",null);
                    int rows=c.getCount();
                    int i=0;
                    if(c.moveToFirst()) {
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

                                message = "Hi " + c3.getString(1) + ". The poll " + pnames.get(k) + " voting has ended.";
                                //i++;
                                c4 = db.rawQuery("UPDATE allnotifications SET context = '" + message + "', notiftype='End' WHERE uid ='" + Integer.valueOf(Uid) + "' AND pid='" + Integer.valueOf(PIDs.get(k)) + "'", null);
                                c4.moveToFirst();
                                c4.close();
                            }
                        }
                    }
                }
                else{
                    check = false;
                }
        Cursor c = db.rawQuery("SELECT * FROM allnotifications WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='End'",null);
        int rows=c.getCount();
        i=0;
        if(c.moveToFirst()){
            do{
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
            }while(i<rows);
            c.close();
            //Cursor cc = db.rawQuery("UPDATE allnotifications SET notiftype='notEnd' WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='Start'", null);
           // cc.moveToFirst();
            //cc.close();
            Cursor cc = db.rawQuery("DELETE FROM allnotifications WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='End'", null);
            cc.moveToFirst();
            cc.close();


        }
            return check;
    }

    public void LogOff(View view) {
        Intent intent = null;
        intent = new Intent(this, MainActivity.class);
        Toast.makeText(this,"You've logged off",Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}
