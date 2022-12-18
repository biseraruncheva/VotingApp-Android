package com.example.votingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class PollStats extends AppCompatActivity {
    SQLiteDatabase db;
    int i = 0, j = 0, k = 0;
    int sizei=0, sizej=0, sizek=0;
    ArrayList<PieEntry> entries = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    PieChart PC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_stats);
        db = openOrCreateDatabase("votingapp", MODE_PRIVATE, null);
        Intent intent = getIntent();
        String Pid = intent.getStringExtra("pid");


        Cursor c = db.rawQuery("SELECT * FROM question WHERE pid = '" + Integer.valueOf(Pid) + "' ", null);
        sizei=c.getCount();
        i=0;
        if (c.moveToPosition(0)) {
            do {
                PC = new PieChart(this);
                LinearLayout LL = findViewById(R.id.LLstats);

                PC.setCenterText("Q."+i+" "+c.getString(2));
                Cursor c1 = db.rawQuery("SELECT * FROM answer WHERE qid = '" + Integer.valueOf(c.getString(0)) + "' ", null);
                sizej=c1.getCount();
                i++;
                j=0;
                if (c1.moveToPosition(0)) {
                    do {
                        Cursor c2 = db.rawQuery("SELECT * FROM myanswer WHERE aid = '" + Integer.valueOf(c1.getString(0)) + "' ", null);
                        sizek=c2.getCount();
                        j++;

                       if (sizek >0) {
                            if (c2.moveToPosition(0)) {


                                        Cursor c3 = db.rawQuery("SELECT COUNT(uid) FROM myanswer WHERE aid = '" + Integer.valueOf(c2.getString(0)) + "' ", null);
                                        c3.moveToFirst();
                                        entries.add(new PieEntry(Integer.valueOf( c3.getString(0)), c1.getString(2)));

                                    k++;
                                    c3.close();

                            }

                        }
                        c2.close();


                        c1.moveToPosition(j);
                    } while (j<sizej);
                    c1.close();


                }

                c.moveToPosition(i);
                setupPieChart();
                PieDataSet dataSet = new PieDataSet(List.copyOf(entries), "Answers");
                dataSet.setColors(colors);
                PieData data = new PieData(dataSet);
                PC.setData(data);
                PC.invalidate();
                PC.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        700));
                PC.setBackgroundResource(R.drawable.my_border);
                LinearLayout LL2 = findViewById(R.id.LLstats);
                LL2.addView(PC);
                entries.clear();
            }while (i<sizei);
            c.close();
        }
    }
    private void setupPieChart() {
        PC.setDrawHoleEnabled(true);
        PC.setUsePercentValues(true);
        PC.setEntryLabelTextSize(12);
        PC.setEntryLabelColor(Color.BLACK);
        //PC.setCenterTextSize(24);
        PC.getDescription().setEnabled(false);

        Legend l = PC.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

    }
}