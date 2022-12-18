package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class AdminAddPoll extends AppCompatActivity {
    SQLiteDatabase db;
    TextView pollname;
    EditText key;
    int firsttime = 1;
    String usern;
    List<EditText> values = new ArrayList<EditText>();
    LinkedHashMap<EditText, List<EditText>> QnA = new LinkedHashMap<EditText,List<EditText>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_poll);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        usern = intent.getStringExtra("username");
    }
    int i=0;
    int j=0;
    int totalq = 0;
    int totala = 0;
    public void generateQuestion(View view) {
        j=0;
        if (firsttime != 1) {
            QnA.put(key,List.copyOf(values));
            if(values.size() > 0) {
                generateQuestionHelper();
            }
            else{
                Toast.makeText(this,"Please add answers before adding another question.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            generateQuestionHelper();
        }

    }

    public void generateQuestionHelper(){
        values.clear();
        LinearLayout LL = (LinearLayout) findViewById(R.id.LLid);
        TextView tv1 = new TextView(this);
        EditText et1 = new EditText(this);
        int k=i + 1;
        tv1.setText("Question No. "+k);
        //tv1.setHint("Question No. "+k);
        et1.setHint("Question No. "+k);
        et1.setId(k);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(40, 0, 40, 0);

        tv1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        et1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        LL.addView(tv1);
        LL.addView(et1, layoutParams);
        key=et1;
        i++;
        totalq++;
        firsttime = 0;
    }

    public void generateAnswer(View view) {
        if(key == null) {
            Toast.makeText(this,"Please add question before adding answers",Toast.LENGTH_LONG).show();
        }
        else {
            LinearLayout LL = (LinearLayout) findViewById(R.id.LLid);
            TextView tv = new TextView(this);
            EditText et = new EditText(this);
            int k = j + 1;
            tv.setText("Answer No. " + k);

            et.setHint("Answer No. " + k);
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            et.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(80, 0, 40, 0);
            LL.addView(tv);
            LL.addView(et, layoutParams);
            values.add(et);
            j++;
            totala++;
        }
    }

    public void addPoll(View view) {
        boolean go = true;
        int k;

        QnA.put(key,List.copyOf(values));
        Set<EditText> ques = QnA.keySet();
        pollname = (TextView) findViewById(R.id.pollname);

        if(pollname.getText().toString().isEmpty()){
            Toast.makeText(this,"Please input name of poll.",Toast.LENGTH_LONG).show();
            go = false;
        }
        else{

        if(ques.contains(null)){
            //Toast.makeText(this,"Please input the questions", Toast.LENGTH_LONG).show();
            go = false;
        }else {
            for (EditText pom1 : ques) {
                List<EditText> sub1 = QnA.get(pom1);
                if (pom1.getText().toString().isEmpty()) {
                    go = false;
                    break;
                }
                for (k = 0; k < sub1.size(); k++) {
                    if (sub1.get(k).getText().toString().isEmpty()) {
                        go = false;

                        break;
                    }
                }
            }
        }
        }


       if(go == true){
            i = 1;
            ContentValues columns = new ContentValues();
            columns.put("name", pollname.getText().toString());
            columns.put("status", "Not started");
            int pid = (int) db.insert("poll", null, columns);
            //db.execSQL("INSERT INTO poll (name) VALUES('"+pollname.getText().toString()+"' );");
            for (EditText pom : ques) {
                columns = new ContentValues();
                columns.put("pid", pid);
                columns.put("que", pom.getText().toString());
                int qid = (int) db.insert("question", null, columns);
                //db.execSQL("INSERT INTO question (pid, que) VALUES('" + 1 + "','"+pom.getText().toString()+"' );");
                List<EditText> sub = QnA.get(pom);
                for (j = 0; j < sub.size(); j++) {
                    db.execSQL("INSERT INTO answer (qid, ans) VALUES('" + qid + "','" + sub.get(j).getText().toString() + "' );");

                }
                i++;
            }
            Intent intent = new Intent(this, AdminHome.class);
            intent.putExtra("username", usern);
            startActivity(intent);
        }else
       {
           Toast.makeText(this,"Please input rest of empty fields.", Toast.LENGTH_LONG).show();

       }
    }




}