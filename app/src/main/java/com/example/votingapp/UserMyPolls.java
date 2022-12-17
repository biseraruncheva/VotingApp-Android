package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UserMyPolls extends AppCompatActivity {
    SQLiteDatabase db;
    int i=0;
    int j=0;
    ArrayList<String> PIDs = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_polls);
        Intent intent = getIntent();
        String Uid = intent.getStringExtra("userid");
       // TextView text = (TextView) findViewById(R.id.WelcomeUserID);
        //text.setText("WELCOME "+usern);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Cursor c1 = db.rawQuery("SELECT * FROM (poll, myanswer) WHERE myanswer.uid = '"+Integer.valueOf(Uid)+"' AND poll.status='Finished'",null);
        int rows = c1.getCount();
        if (c1.moveToPosition(0)) {
            do {
                LinearLayout LL = (LinearLayout) findViewById(R.id.LLmypolls);
                TextView tv = new TextView(this);
                PIDs.add(c1.getString(0));
                tv.setText("\n"+c1.getString(0)+". "+c1.getString(1)+" \n\n Status: "+c1.getString(2));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv.setBackgroundResource(R.drawable.my_border);
                final float scale = UserMyPolls.this.getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
                tv.setId(i);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //c1.moveToPosition(view.getId());
                        Intent intent = new Intent(getApplicationContext(), PollStats.class);
                        // intent.putExtra("pollname", c1.getString(1));
                        intent.putExtra("pid", PIDs.get(view.getId()));
                        //intent.putExtra("userid", Uid);
                        // intent.putExtra("numpolls", c1.getCount());
                        startActivity(intent);
                    }
                });
                LL.addView(tv);
                j++;
                c1.moveToPosition(i);
            } while (j<rows);
            c1.close();
        }
        else
        {
            LinearLayout LL = (LinearLayout) findViewById(R.id.LLmypolls);
            TextView tv = new TextView(this);
            tv.setText("NO ADDED POLLS YET");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            //tv.setBackgroundResource(R.drawable.my_border);
            final float scale = UserMyPolls.this.getApplicationContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (100 * scale + 0.5f);
            tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
            LL.addView(tv);
            c1.close();
        }





    }
}