package com.example.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UserPollAnswers extends AppCompatActivity {
    int i=0;
    SQLiteDatabase db;
    String Pid;
    String Qid;
    String Uid;
    String q;
    String checkedRadio;
    double lat;
    double log;
    //String checkedID;
    ArrayList<String> AIDs = new ArrayList<String>();
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    CountDownTimer cdt;
    long start_millis;
    long end_millis;
    long total_millis;
    boolean TimerRunning = true;
    Cursor c3;
    TextView timerTextView;
    Random rn = new Random();
    ArrayList<String> PIDs = new ArrayList<String>();
    ArrayList<String> pnames = new ArrayList<>();
    String pname;
    String usern;
    RadioGroup RG;
    LinearLayout L1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll_answers);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        q = intent.getStringExtra("question");
        Pid = intent.getStringExtra("pid");
        Uid = intent.getStringExtra("userid");
        usern = intent.getStringExtra("username");
        pname = intent.getStringExtra("pollname");
        String QPosition = intent.getStringExtra("qposition");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        L1 = (LinearLayout) findViewById(R.id.QAfield);
        TextView tv = new TextView(this);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        //tv.setBackgroundResource(R.drawable.my_border);
        Cursor c = db.rawQuery("SELECT qid FROM question WHERE  pid = '" +Integer.valueOf(Pid)+"' AND que ='" +q+"'", null);
        c.moveToFirst();
        Qid = c.getString(0);
        c.close();
        tv.setText(QPosition+". "+q);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(40, 15, 40, 15);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        L1.addView(tv, layoutParams);

        Cursor c1 = db.rawQuery("SELECT * FROM answer WHERE  qid = '" +Integer.valueOf(Qid)+"'", null);
        int rows = c1.getCount();
        RG = new RadioGroup(this);

        LinearLayout LL = (LinearLayout) findViewById(R.id.QAfield);
        if (c1.moveToPosition(0)) {
            do {
                RadioButton rb = new RadioButton(this);
                rb.setText(c1.getString(2));
                AIDs.add(c1.getString(0));
                //rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                //rb.setBackgroundResource(R.drawable.my_border);
                final float scale = UserPollAnswers.this.getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
                rb.setId(i);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
                rb.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                       // c1.moveToPosition(view.getId());
                        Cursor c2 = db.rawQuery("SELECT * FROM myanswer WHERE  qid = '" +Integer.valueOf(Qid)+"'AND pid = '"+Integer.valueOf(Pid)+"'AND  uid ='"+Integer.valueOf(Uid)+"'", null);
                        Cursor c3 = db.rawQuery("SELECT * FROM myinfo WHERE pid = '"+Integer.valueOf(Pid)+"' AND  uid ='"+Integer.valueOf(Uid)+"'", null);
                        if (c2.moveToFirst())
                        {


                            long time = System.currentTimeMillis();
                            c2=db.rawQuery("UPDATE myanswer SET aid = '"+Integer.valueOf(Integer.valueOf(AIDs.get(view.getId())))+"' WHERE  qid = '" +Integer.valueOf(Qid)+"'AND pid = '"+Integer.valueOf(Pid)+"' AND uid ='"+Integer.valueOf(Uid)+"'",null);
                            c2.moveToFirst();
                            c2.close();
                            c3=db.rawQuery("UPDATE myinfo SET time = '"+time+"', latitude = '"+lat+"', longitude = '"+log+"' WHERE  pid = '"+Integer.valueOf(Pid)+"' AND uid ='"+Integer.valueOf(Uid)+"'",null);
                            c3.moveToFirst();
                            c3.close();
                            Toast.makeText(UserPollAnswers.this,"Your answer was updated", Toast.LENGTH_LONG).show();


                        }
                        else
                        {

                            int answerid = Integer.valueOf(AIDs.get(view.getId()));
                            int questionid = Integer.valueOf(Qid);
                            int userid = Integer.valueOf(Uid);
                            int pollid = Integer.valueOf(Pid);
                            long time = System.currentTimeMillis();
                            db.execSQL("INSERT INTO myanswer VALUES('"+answerid+"', '"+questionid+"' ,'"+pollid+"' ,'"+userid+"');");
                            c2.close();
                            db.execSQL("INSERT INTO myinfo VALUES('"+userid+"', '"+pollid+"' ,'"+time+"' ,'"+lat+"','"+log+"');");
                            c3.close();
                            Toast.makeText(UserPollAnswers.this,"Your answer was saved", Toast.LENGTH_LONG).show();


                        }

                    }
                });
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(40, 15, 40, 15);
                rb.setLayoutParams(params);
                RG.addView(rb);
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
            c1.close();;
        }

        L1.setBackgroundResource(R.drawable.my_border);
        L1.addView(RG);

    }

    public void backToQuestions(View view) {
        Intent intent = new Intent(this, UserPollQuestions.class);
        intent.putExtra("question", q);
        intent.putExtra("pid", Pid);
        intent.putExtra("qid",Qid);
        intent.putExtra("userid", Uid);
        intent.putExtra("username", usern);
        //intent.putExtra("checkedradio",checkedRadio);


        startActivity(intent);

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //latitudeTextView.setText(location.getLatitude() + "");
                            lat = location.getLatitude();
                            log = location.getLongitude();
                            //longitTextView.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(UserPollAnswers.this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
           // longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
            lat = mLastLocation.getLatitude();
            log = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
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
                TextView timerTextView = (TextView) findViewById(R.id.dispTimerAnswer);


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
                Intent intent = new Intent(UserPollAnswers.this, UserHome.class);
                intent.putExtra("userid", Uid);
                intent.putExtra("pid", Pid);
                intent.putExtra("pollname", pname);
                intent.putExtra("username",usern);
                TextView timerTextView = (TextView) findViewById(R.id.dispTimerAnswer);
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
        timerTextView = findViewById(R.id.dispTimerAnswer);
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


}