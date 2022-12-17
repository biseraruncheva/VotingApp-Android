package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText usern;
    EditText passw;
    String Uid;
    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = true;
    Cursor c3;
    Cursor c;
    ArrayList<String> pids = new ArrayList<>();
    ArrayList<String> pnames = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);


    }

    public void ClLogin(View view) {
        usern = (EditText) findViewById(R.id.loginemail);
        passw = (EditText) findViewById(R.id.loginpassword);
        String s1=usern.getText().toString();
        String s2=passw.getText().toString();

        Cursor c1 = db.rawQuery("SELECT * FROM user WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);
        Cursor c2 = db.rawQuery("SELECT * FROM admin WHERE  username = '" + usern.getText().toString() + "' AND password = '"+passw.getText().toString()+"'", null);

        if (c1.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, UserHome.class);
            intent.putExtra("username", usern.getText().toString());
            intent.putExtra("userid", c1.getString(2));
            Uid = c1.getString(2);
            c1.close();
            c2.close();
            addNotification();
            startActivity(intent);
        }
        else if (c2.moveToFirst()) {
            Intent intent = null;
            intent = new Intent(this, AdminHome.class);
            intent.putExtra("username", usern.getText().toString());
            c2.close();
            c1.close();
            startActivity(intent);
        }
    }
    public void addNotification() {
        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            mChannel.setDescription(Description);
            //mChannel.enableLights(true);
            //mChannel.setLightColor(Color.RED);
            //mChannel.enableVibration(true);
            //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //mChannel.setShowBadge(true);
            manager.createNotificationChannel(mChannel);
        }

        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM allnotifications, poll WHERE uid = '"+Integer.valueOf(Uid)+"' AND allnotifications.pid = poll.pid ",null);
        int rows=c.getCount();
        int i=0;
        if(c.moveToFirst()){
            do{
                    pids.add(c.getString(4));
                    pnames.add(c.getString(6));
                    i++;
                    c.moveToPosition(i);

            }while(i<rows);
            c.close();

            int k;
            for(k=0; k<pids.size(); k++) {
                end_millis = prefs.getLong("endmillis" + pids.get(k), 600000);
                start_millis = prefs.getLong("startmillis" + pids.get(k), 6000000);
                total_millis = end_millis - System.currentTimeMillis();

                if (total_millis < 0) {
                    total_millis = 0;
                    TimerRunning = false;
                    db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
                    c3 = db.rawQuery("UPDATE poll SET status = 'Finished' WHERE pid = '" + Integer.valueOf(pids.get(k)) + "'", null);
                    c3.moveToFirst();
                    c3.close();
                    c3 = db.rawQuery("SELECT uid, username FROM user WHERE uid = '"+Integer.valueOf(Uid)+"'", null);
                    c3.moveToFirst();

                            String message = "Hi "+c3.getString(1)+". The poll "+pnames.get(k)+" voting has ended.";
                            //i++;
                            Cursor c4 = db.rawQuery("UPDATE allnotifications SET context = '"+message+"', notiftype='End' WHERE uid ='"+Integer.valueOf(Uid)+"' AND pid='"+Integer.valueOf(pids.get(k))+"'",null);
                            c4.moveToFirst();
                            c4.close();

                    }

                }
            }
            c = db.rawQuery("SELECT * FROM allnotifications WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='Start' OR notiftype='End'",null);
            rows=c.getCount();
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
                Cursor cc = db.rawQuery("UPDATE allnotifications SET notiftype='notEnd' WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='Start'", null);
                cc.moveToFirst();
                cc.close();
                cc = db.rawQuery("DELETE FROM allnotifications WHERE uid = '"+Integer.valueOf(Uid)+"' AND notiftype='End'", null);
                cc.moveToFirst();
                cc.close();


            }



        }

    }


