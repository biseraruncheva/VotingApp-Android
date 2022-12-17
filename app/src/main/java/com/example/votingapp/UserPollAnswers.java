package com.example.votingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll_answers);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        q = intent.getStringExtra("question");
        Pid = intent.getStringExtra("pid");
        Uid = intent.getStringExtra("userid");
        String QPosition = intent.getStringExtra("qposition");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        LinearLayout L1 = (LinearLayout) findViewById(R.id.QAfield);
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
        RadioGroup RG = new RadioGroup(this);

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

                            // method to get the location

                            long time = System.currentTimeMillis();
                            c2=db.rawQuery("UPDATE myanswer SET aid = '"+Integer.valueOf(Integer.valueOf(AIDs.get(view.getId())))+"' WHERE  qid = '" +Integer.valueOf(Qid)+"'AND pid = '"+Integer.valueOf(Pid)+"' AND uid ='"+Integer.valueOf(Uid)+"'",null);
                            c2.moveToFirst();
                            c2.close();
                            c3=db.rawQuery("UPDATE myinfo SET time = '"+time+"', latitude = '"+lat+"', longitude = '"+log+"' WHERE  pid = '"+Integer.valueOf(Pid)+"' AND uid ='"+Integer.valueOf(Uid)+"'",null);
                            c3.moveToFirst();
                            c3.close();
                            //Toast.makeText(UserPollAnswers.this, "Lat: "+lat+" Long: "+log, Toast.LENGTH_SHORT).show();


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
                            //Toast.makeText(UserPollAnswers.this, "Lat: "+lat+" Long: "+log, Toast.LENGTH_SHORT).show();


                        }
                        //checkedRadio = c1.getString(0);


                        //Intent intent = new Intent(getApplicationContext(), AdminPollDetails.class);
                        //intent.putExtra("pollname", c1.getString(1));
                        //intent.putExtra("pid", c1.getString(0));
                        //intent.putExtra("numpolls", c1.getCount());
                        //startActivity(intent);

                      //  Toast.makeText(UserPollAnswers.this, "You clicked on row number "+view.getId() +"and it says "+c1.getString(2)+"it has id in database"+c1.getString(0), Toast.LENGTH_SHORT).show();

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
}