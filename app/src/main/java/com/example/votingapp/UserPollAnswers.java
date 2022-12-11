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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class UserPollAnswers extends AppCompatActivity {
    int i=0;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll_answers);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        String question = intent.getStringExtra("question");
        String Qid = intent.getStringExtra("qid");
        LinearLayout L1 = (LinearLayout) findViewById(R.id.QAfield);
        TextView tv = new TextView(this);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setBackgroundResource(R.drawable.my_border);
        tv.setText(question);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        L1.addView(tv);

        Cursor c1 = db.rawQuery("SELECT * FROM answer WHERE  qid = '" +Integer.valueOf(Qid)+"'", null);
        int rows = c1.getCount();
        RadioGroup RG = new RadioGroup(this);
        LinearLayout LL = (LinearLayout) findViewById(R.id.QAfield);
        if (c1.moveToPosition(0)) {
            do {
                RadioButton rb = new RadioButton(this);
                rb.setText(c1.getString(2));
                //rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                //rb.setBackgroundResource(R.drawable.my_border);
                final float scale = UserPollAnswers.this.getApplicationContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (100 * scale + 0.5f);
                rb.setId(i);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT));
                rb.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        c1.moveToPosition(view.getId());

                        //Intent intent = new Intent(getApplicationContext(), AdminPollDetails.class);
                        //intent.putExtra("pollname", c1.getString(1));
                        //intent.putExtra("pid", c1.getString(0));
                        //intent.putExtra("numpolls", c1.getCount());
                        //startActivity(intent);
                        Toast.makeText(UserPollAnswers.this, "You clicked on row number "+view.getId() +"and it says "+c1.getString(2), Toast.LENGTH_SHORT).show();


                    }
                });
                RG.addView(rb);
                i++;
                c1.moveToPosition(i);
            } while (i<rows);
        }
        LL.addView(RG);

    }
}