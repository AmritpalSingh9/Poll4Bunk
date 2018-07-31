package com.armyof2.poll4bunk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.android.job.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.armyof2.poll4bunk.LaunchActivity.SERVER_ID;
import static com.armyof2.poll4bunk.LaunchActivity.name;
import static com.armyof2.poll4bunk.PieChartActivity.scheduleJob;
import static com.armyof2.poll4bunk.SignInActivity.userUid;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> bunkYes, bunkNo, bunkYes80, bunkUndec;
    public static ArrayList<String> BUNK_YES, BUNK_NO, BUNK_YES80, BUNK_UNDEC;
    public static String totalpeeps = "0";
    public static int x = 1;
    public static float posp = -1;
    private TextView dateView;
    //private TextView titleView;
    private TextView cdtimerView;
    private TextView adminView;
    private TextView questView;
    private int option = 4;
    int seconds , minutes;
    public static String i = "0", j = "0", k = "0", l = "0", m = "0";
    private static final String FORMAT = "%02d:%02d:%02d";
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2, myRef3;
    private ArrayList<String> bunkServerStuff;
    private ArrayList<String> bunkServerVotes;
    private RadioButton r1, r2, r3, r4;
    private String hasVoted = "yolo", added = "yolo";
    private CountDownTimer cdTimer;
    private long timeDifference = 0;
    final long timeInterval = 10;
    final long timeInterval2 = 2000;
    public static String servTitle;
    private String bunkWait = "false";
    private boolean stop = false;
    private boolean connect = true;
    private boolean ov = true;
    private boolean dest = true;


    //------------------pie-----------------
    public static int[] yData = {Integer.parseInt(i), Integer.parseInt(j), Integer.parseInt(k), Integer.parseInt(l)};
    public static PieChart pieChart;
    Description description;
    //---------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());
        adjustDisplayScale( getResources().getConfiguration());
        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();

        getWindow().addContentView(inflater.inflate(R.layout.activity_piechart, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        JobManager.create(this).addJobCreator(new MyJobCreator());

        questView = (TextView) findViewById(R.id.tv_quest);

        //-----------------pie-----------------------------------------------
        pieChart = (PieChart) findViewById(R.id.piechart);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)questView.getLayoutParams();
        params.addRule(RelativeLayout.BELOW,pieChart.getId());
        questView.setLayoutParams(params);
        description = new Description();
        description.setText("");
        description.setTextSize(14);
        //description.setPosition(600, 615);

        //set props
        pieChart.setHoleRadius(45f);
        pieChart.setHoleColor(424242);
        pieChart.setTransparentCircleAlpha(50);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterTextSize(15);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setDescription(description);
        //pieChart.setDrawEntryLabels(true);
        pieChart.setRotationEnabled(true);

        //add data set
        addDataSet(pieChart, yData);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                posp = h.getX();
                CustomDialogBox cdd=new CustomDialogBox(MainActivity.this);
                cdd.show();
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //-------------------------------------------------------------------------------

        dateView = (TextView) findViewById(R.id.tv_date);
        //titleView = (TextView) findViewById(R.id.tv_title);
        cdtimerView = (TextView) findViewById(R.id.tv_cdtimer);
        adminView = (TextView) findViewById(R.id.tv_admin);
        r1 = (RadioButton) findViewById(R.id.rad_op1);
        r2 = (RadioButton) findViewById(R.id.rad_op2);
        r3 = (RadioButton) findViewById(R.id.rad_op3);
        r4 = (RadioButton) findViewById(R.id.rad_op4);

        r1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                option = 1;
                r1.setChecked(true);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                option = 2;
                r1.setChecked(false);
                r2.setChecked(true);
                r3.setChecked(false);
                r4.setChecked(false);
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                option = 3;
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(true);
                r4.setChecked(false);
            }
        });

        r4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                option = 4;
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(true);
            }
        });

        bunkServerStuff = new ArrayList<>();
        bunkServerVotes = new ArrayList<>();
        bunkYes = new ArrayList<>();
        bunkNo = new ArrayList<>();
        bunkYes80 = new ArrayList<>();
        bunkUndec = new ArrayList<>();

        BUNK_YES = new ArrayList<>();
        BUNK_NO = new ArrayList<>();
        BUNK_YES80 = new ArrayList<>();
        BUNK_UNDEC = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(SERVER_ID);
        myRef2 = database.getReference().child(SERVER_ID).child("YuserUIDs");
        myRef3 = database.getReference().child(SERVER_ID).child("Bunker's Name");

        myRef.child("Yes");
        myRef.child("No");
        myRef.child("Yes80");
        myRef.child("Undec");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.getKey().equals("YuserUIDs") && !dataSnapshot.getKey().equals("Bunker's Name") && !dataSnapshot.getKey().equals("Zmessages")) {
                    String value = dataSnapshot.getValue(String.class);
                    bunkServerStuff.add(value);
                    Log.d("TAG", "onChildAdded: " + bunkServerStuff);
                    startTimer();
                    if (bunkServerStuff.size() == 4) {
                        dateView.setText(bunkServerStuff.get(0));
                        //titleView.setText(bunkServerStuff.get(2));
                        totalpeeps = bunkServerStuff.get(1);
                        adminView.setText(bunkServerStuff.get(3));
                        setTitle(bunkServerStuff.get(2));
                        servTitle = bunkServerStuff.get(2);
                    }
                    bunkServerVotes.add(value);
                    if (dataSnapshot.getKey().equals("Bunk Wait")) {
                        if (value.equals("true"))
                            bunkWait = "true";
                        else
                            bunkWait = "false";
                    }
                    if (dataSnapshot.getKey().equals("Yes")) {
                        i = value;
                        Log.d("TAG", "i = " + i);
                    }
                    if (dataSnapshot.getKey().equals("No"))
                        j = value;
                    if (dataSnapshot.getKey().equals("Yes80"))
                        k = value;
                    if (dataSnapshot.getKey().equals("Undec"))
                        l = value;
                }
                    //Log.d("TAG", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.getKey().equals("YuserUIDs") && !dataSnapshot.getKey().equals("Bunker's Name") && !dataSnapshot.getKey().equals("Zmessages")) {
                    String value = dataSnapshot.getValue(String.class);
                    bunkServerVotes.add(value);
                   /* if (dataSnapshot.getKey().equals("Bunk Wait")) {
                        if (value.equals("true"))
                            bunkWait = "true";
                        else
                            bunkWait = "false";
                    }*/
                   /* if (dataSnapshot.getKey().equals("Yes"))
                        i = value;
                    if (dataSnapshot.getKey().equals("No"))
                        j = value;
                    if (dataSnapshot.getKey().equals("Yes80"))
                        k = value;
                    if (dataSnapshot.getKey().equals("Undec"))
                        l = value;
                    Log.d("TAG", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l); */

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                i = "0";
                j = "0";
                k = "0";
                l = "0";
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String valu = child.getValue(String.class);
                    Log.d("TAG", "valu = " + valu);
                    if (valu.equals("yes")) {
                        int tempo;
                        tempo = Integer.parseInt(i);
                        tempo++;
                        i = Integer.toString(tempo);
                    }
                    if (valu.equals("no")) {
                        int tempo;
                        tempo = Integer.parseInt(j);
                        tempo++;
                        j = Integer.toString(tempo);
                    }
                    if (valu.equals("yes80")) {
                        int tempo;
                        tempo = Integer.parseInt(k);
                        tempo++;
                        k = Integer.toString(tempo);
                    }
                    if (valu.equals("undec")) {
                        int tempo;
                        tempo = Integer.parseInt(l);
                        tempo++;
                        l = Integer.toString(tempo);
                    }
                }
                Log.d("TAG", "i = " + i + ", j = " + j + ", k = " + k + ", l = " + l);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final String value = dataSnapshot.getValue(String.class);
                if (dataSnapshot.getKey().equals(userUid)) {
                    hasVoted = value;
                    Log.d("TAG", "onChildAdded : hasVoted = " + hasVoted);
                }

                    myRef3.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot3, String s) {
                            String val = dataSnapshot3.getValue(String.class);
                            Log.d("TAG", "value = " + value);
                            if(dataSnapshot.getKey().equals(dataSnapshot3.getKey())) {
                                if (value.equals("yes")) {
                              /*      if (added.equals("no") && !name.equals(val));
                                       // bunkNo.remove(bunkNo.size() - 1);
                                    else if (added.equals("yes80") && !name.equals(val));
                                       // bunkYes80.remove(bunkYes80.size() - 1);
                                    else if (added.equals("undec") && !name.equals(val));
                                       // bunkUndec.remove(bunkUndec.size() - 1); */
                                    bunkYes.add(val);
                                    Log.d("TAG", "bunkYesAddedAdded = " + bunkYes);
                                    BUNK_YES = bunkYes;
                                    added = "yes";
                                } else if (value.equals("no")) {
                                /*    if (added.equals("yes") && !name.equals(val));
                                       // bunkYes.remove(bunkYes.size() - 1);
                                    else if (added.equals("yes80") && !name.equals(val));
                                      //  bunkYes80.remove(bunkYes80.size() - 1);
                                    else if (added.equals("undec") && !name.equals(val));
                                       // bunkUndec.remove(bunkUndec.size() - 1); */
                                    bunkNo.add(val);
                                    Log.d("TAG", "bunkNoAddedAdded = " + bunkNo);
                                    BUNK_NO = bunkNo;
                                    added = "no";
                                } else if (value.equals("yes80")) {
                                /*    if (added.equals("no") && !name.equals(val));
                                       // bunkNo.remove(bunkNo.size() - 1);
                                    else if (added.equals("yes") && !name.equals(val));
                                      //  bunkYes.remove(bunkYes.size() - 1);
                                    else if (added.equals("undec") && !name.equals(val));
                                       // bunkUndec.remove(bunkUndec.size() - 1); */
                                    bunkYes80.add(val);
                                    Log.d("TAG", "bunkYes80AddedAdded = " + bunkYes80);
                                    BUNK_YES80 = bunkYes80;
                                    added = "yes80";
                                } else if (value.equals("undec")) {
                                 /*   if (added.equals("no") && !name.equals(val));
                                      //  bunkNo.remove(bunkNo.size() - 1);
                                    else if (added.equals("yes80") && !name.equals(val));
                                       // bunkYes80.remove(bunkYes80.size() - 1);
                                    else if (added.equals("yes") && !name.equals(val));
                                       // bunkYes.remove(bunkYes.size() - 1); */
                                    bunkUndec.add(val);
                                    Log.d("TAG", "bunkUndecAddedAdded = " + bunkUndec);
                                    BUNK_UNDEC = bunkUndec;
                                    added = "undec";
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                Log.d("TAG", "change vote ");
                final String value = dataSnapshot.getValue(String.class);
                if (dataSnapshot.child("YuserUIDs").getKey().equals(userUid)) {
                    hasVoted = value;
                    Log.d("TAG", "onChildChanged : hasVoted = " + hasVoted);
                }


                    myRef3.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot3, String s) {
                            String val = dataSnapshot3.getValue(String.class);
                            if (dataSnapshot.getKey().equals(dataSnapshot3.getKey())) {
                                if (value.equals("yes")) {
                                    /*if (added.equals("no") && name.equals(val))
                                        removeStringFromArraylist(val, bunkNo);
                                    else if (added.equals("yes80") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes80);
                                    else if (added.equals("undec") && name.equals(val))
                                        removeStringFromArraylist(val, bunkUndec);*/
                                    bunkYes.add(val);
                                    Log.d("TAG", "bunkYesChangedAdded = " + bunkYes);
                                    BUNK_YES = bunkYes;
                                    voteHandler(value);
                                    Log.d("TAG", "bunkYESChangedAdded = " + BUNK_YES);
                                    added = "yes";
                                } else if (value.equals("no")) {
                                    /*if (added.equals("yes") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes);
                                    else if (added.equals("yes80") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes80);
                                    else if (added.equals("undec") && name.equals(val))
                                        removeStringFromArraylist(val, bunkUndec);*/
                                    bunkNo.add(val);
                                    Log.d("TAG", "bunkNoChangedAdded = " + bunkNo);
                                    BUNK_NO = bunkNo;
                                    voteHandler(value);
                                    Log.d("TAG", "bunkNOChangedAdded = " + BUNK_NO);
                                    added = "no";
                                } else if (value.equals("yes80")) {
                                    /*if (added.equals("no") && name.equals(val))
                                        removeStringFromArraylist(val, bunkNo);
                                    else if (added.equals("yes") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes);
                                    else if (added.equals("undec") && name.equals(val))
                                        removeStringFromArraylist(val, bunkUndec);*/
                                    bunkYes80.add(val);
                                    Log.d("TAG", "bunkYes80ChangedAdded = " + bunkYes80);
                                    BUNK_YES80 = bunkYes80;
                                    voteHandler(value);
                                    Log.d("TAG", "bunkYES80ChangedAdded = " + BUNK_YES80);
                                    added = "yes80";
                                } else if (value.equals("undec")) {
                                    /*if (added.equals("no") && name.equals(val))
                                        removeStringFromArraylist(val, bunkNo);
                                    else if (added.equals("yes80") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes80);
                                    else if (added.equals("yes") && name.equals(val))
                                        removeStringFromArraylist(val, bunkYes);*/
                                    bunkUndec.add(val);
                                    Log.d("TAG", "bunkUndecChangedAdded = " + bunkUndec);
                                    BUNK_UNDEC = bunkUndec;
                                    voteHandler(value);
                                    Log.d("TAG", "bunkUNDECChangedAdded = " + BUNK_UNDEC);
                                    added = "undec";
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Pie chart
        //scheduleJob();
        Thread thread = new Thread(runnable);
        thread.start();
        //Thread thread2 = new Thread(runnable2);
        //thread2.start();
    }

    public void removeStringFromArraylist(String s, ArrayList<String> list)
    {
        for (int i = 0; i < list.size(); i++){
            if(s.equals(list.get(i)))
                list.remove(i);
        }
    }

    public void onConfirmButtonClicked(View view) {
        if(!isNetworkAvailable(this)) {
            Toast.makeText(this, "Are you connected to the internet?", Toast.LENGTH_SHORT).show();
            connect = false;
            return;
        }

        if(!connect) {
            Toast.makeText(this, "Please re-fetch servers and join again", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        /*if(bunkWait.equals("true")) {
            Toast.makeText(this, "Wait!", Toast.LENGTH_SHORT).show();
            return;
        } else
            myRef.child("Bunk Wait").setValue("true");*/
        switch (option){
            case 1:
                int p = Integer.parseInt(i);
                ++p;
                int q = Integer.parseInt(j);
                --q;
                int r = Integer.parseInt(k);
                --r;
                int s = Integer.parseInt(l);
                --s;

                if(hasVoted.equals("yes")) {
                    break;
                } else if(hasVoted.equals("no")){
                    myRef.child("No").setValue(Integer.toString(q));
                } else if(hasVoted.equals("yes80")){
                    myRef.child("Yes80").setValue(Integer.toString(r));
                } else if(hasVoted.equals("undec")){
                    myRef.child("Undec").setValue(Integer.toString(s));
                }
                myRef.child("Yes").setValue(Integer.toString(p));
                myRef.child("YuserUIDs").child(userUid).setValue("yes");
                hasVoted = "yes";
                break;

            case 2:
                p = Integer.parseInt(i);
                --p;
                q = Integer.parseInt(j);
                ++q;
                r = Integer.parseInt(k);
                --r;
                s = Integer.parseInt(l);
                --s;

                if(hasVoted.equals("no")) {
                    break;
                } else if(hasVoted.equals("yes")){
                    myRef.child("Yes").setValue(Integer.toString(p));
                } else if(hasVoted.equals("yes80")){
                    myRef.child("Yes80").setValue(Integer.toString(r));
                } else if(hasVoted.equals("undec")){
                    myRef.child("Undec").setValue(Integer.toString(s));
                }
                myRef.child("No").setValue(Integer.toString(q));
                myRef.child("YuserUIDs").child(userUid).setValue("no");
                hasVoted = "no";
                break;

            case 3:
                p = Integer.parseInt(i);
                --p;
                q = Integer.parseInt(j);
                --q;
                r = Integer.parseInt(k);
                ++r;
                s = Integer.parseInt(l);
                --s;

                if(hasVoted.equals("yes80")) {
                    break;
                } else if(hasVoted.equals("no")){
                    myRef.child("No").setValue(Integer.toString(q));
                } else if(hasVoted.equals("yes")){
                    myRef.child("Yes").setValue(Integer.toString(p));
                } else if(hasVoted.equals("undec")){
                    myRef.child("Undec").setValue(Integer.toString(s));
                }
                myRef.child("Yes80").setValue(Integer.toString(r));
                myRef.child("YuserUIDs").child(userUid).setValue("yes80");
                hasVoted = "yes80";
                break;

            case 4:
                p = Integer.parseInt(i);
                --p;
                q = Integer.parseInt(j);
                --q;
                r = Integer.parseInt(k);
                --r;
                s = Integer.parseInt(l);
                ++s;

                if(hasVoted.equals("undec")) {
                    break;
                } else if(hasVoted.equals("no")){
                    myRef.child("No").setValue(Integer.toString(q));
                } else if(hasVoted.equals("yes80")){
                    myRef.child("Yes80").setValue(Integer.toString(r));
                } else if(hasVoted.equals("yes")){
                    myRef.child("Yes").setValue(Integer.toString(p));
                }
                myRef.child("Undec").setValue(Integer.toString(s));
                myRef.child("YuserUIDs").child(userUid).setValue("undec");
                hasVoted = "undec";
                break;
        }
        //addDataSet(pieChart, yData);
    }

    public void startTimer(){
        String endDate;
        endDate = bunkServerStuff.get(0) + " 00:00:00";
        Date d1 = Calendar.getInstance().getTime();
        Date d2;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            d2 = format.parse(endDate);
            timeDifference = d2.getTime() - d1.getTime();
            Log.d("TAG", "Timer Started: " + timeDifference + "Timer Started: " + d1 + "Timer Started: " + d2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        final long timeDiffInMilis = TimeUnit.MILLISECONDS.toMillis(timeDifference);

        cdTimer = new CountDownTimer(timeDiffInMilis, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                cdtimerView.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                cdtimerView.setText("Polling Over");
                if((timeDiffInMilis + 86400000) >= 0)
                    over();
                else
                    destroy();
            }
        }.start();
    }

    public void over(){
        if(ov) {
            Intent i = new Intent(this, PollingOverActivity.class);
            startActivity(i);
            finish();
            ov = false;
        }
    }

    public void destroy(){
        if(dest) {
            myRef.removeValue();
            Intent i = new Intent(this, LaunchActivity.class);
            startActivity(i);
            finish();
            dest = false;
        }
    }

    //--------------------------pie---------------------------------------------

    public static void addDataSet(PieChart pieChart, int[] yData){

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        m = Integer.toString(Integer.parseInt(i)+Integer.parseInt(j)+Integer.parseInt(k)+Integer.parseInt(l));
        pieChart.setCenterText("Total Voters: " + totalpeeps + "\nVotes Made: " + m);

        for (int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i], i));
        }

        //add data
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(18);
        pieDataSet.setValueTextColor(Color.WHITE);

        //set colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#66cdaa"));
        colors.add(Color.parseColor("#ffb6c1"));
        colors.add(Color.parseColor("#6495ed"));
        colors.add(Color.parseColor("#eedd82"));
        pieDataSet.setColors(colors);


        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        //legend.setForm(Legend.LegendForm.CIRCLE);
        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        //legend.setYEntrySpace(10);


        //create pie obj
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    //---------------------------------------------------------

    Runnable runnable = new Runnable() {
        public void run() {
            while (!stop) {

                try {
                    Thread.sleep(timeInterval);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.d("crap", "PieChartValuesUpdated");
                            yData = new int[]{Integer.parseInt(i), Integer.parseInt(j), Integer.parseInt(k), Integer.parseInt(l)};
                            addDataSet(pieChart, yData);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        stop = true;
        Log.d("TAG", "onDestroy: called");
        super.onDestroy();
    }

    /*Runnable runnable2 = new Runnable() {
        public void run() {
            while (true) {

                try {
                    Thread.sleep(timeInterval2);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.d("crap", "Wait");
                            myRef.child("Bunk Wait").setValue("false");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };*/

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onChatRoomButtonClicked(View view){
        Intent w = new Intent(this, ChatRoomActivity.class);
        startActivity(w);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                Intent i = new Intent(this, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return true;

            case R.id.about:
                i = new Intent(this,Info.class);
                startActivity(i);
                return true;

            case R.id.clog:
                i = new Intent(this, com.armyof2.poll4bunk.Log.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void voteHandler(String v){
        if(v.equals("yes")){
            Log.d("TAG", "voteHandler: yes");
            for (int p = 0; p < BUNK_YES.size(); p++) {
                if (BUNK_NO.contains(BUNK_YES.get(p))) {
                    removeStringFromArraylist(BUNK_YES.get(p), BUNK_NO);
                }
                if (BUNK_YES80.contains(BUNK_YES.get(p))) {
                    removeStringFromArraylist(BUNK_YES.get(p), BUNK_YES80);
                }
                if (BUNK_UNDEC.contains(BUNK_YES.get(p))) {
                    removeStringFromArraylist(BUNK_YES.get(p), BUNK_UNDEC);
                }
            }
        }
        if(v.equals("no")){
            Log.d("TAG", "voteHandler: no");
            for (int p = 0; p < BUNK_NO.size(); p++) {
                if (BUNK_YES.contains(BUNK_NO.get(p))) {
                    removeStringFromArraylist(BUNK_NO.get(p), BUNK_YES);
                }
                if (BUNK_YES80.contains(BUNK_NO.get(p))) {
                    removeStringFromArraylist(BUNK_NO.get(p), BUNK_YES80);
                }
                if (BUNK_UNDEC.contains(BUNK_NO.get(p))) {
                    removeStringFromArraylist(BUNK_NO.get(p), BUNK_UNDEC);
                }
            }
        }
        if(v.equals("yes80")){
            Log.d("TAG", "voteHandler: yes80");
            for (int p = 0; p < BUNK_YES80.size(); p++) {
                if (BUNK_YES.contains(BUNK_YES80.get(p))) {
                    removeStringFromArraylist(BUNK_YES80.get(p), BUNK_YES);
                }
                if (BUNK_NO.contains(BUNK_YES80.get(p))) {
                    removeStringFromArraylist(BUNK_YES80.get(p), BUNK_NO);
                }
                if (BUNK_UNDEC.contains(BUNK_YES80.get(p))) {
                    removeStringFromArraylist(BUNK_YES80.get(p), BUNK_UNDEC);
                }
            }
        }
        if(v.equals("undec")){
            Log.d("TAG", "voteHandler: undec");
            for (int p = 0; p < BUNK_UNDEC.size(); p++) {
                if (BUNK_YES.contains(BUNK_UNDEC.get(p))) {
                    removeStringFromArraylist(BUNK_UNDEC.get(p), BUNK_YES);
                }
                if (BUNK_NO.contains(BUNK_UNDEC.get(p))) {
                    removeStringFromArraylist(BUNK_UNDEC.get(p), BUNK_NO);
                }
                if (BUNK_YES80.contains(BUNK_UNDEC.get(p))) {
                    removeStringFromArraylist(BUNK_UNDEC.get(p), BUNK_YES80);
                }
            }
        }
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration != null && configuration.fontScale != 1.0) {
            configuration.fontScale = (float) 1.0;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            this.getResources().updateConfiguration(configuration, metrics);
        }
    }

    public void adjustDisplayScale(Configuration configuration) {
        if (configuration != null) {
            Log.d("TAG", "adjustDisplayScale: " + configuration.densityDpi);
            if(configuration.densityDpi >= 485)
                configuration.densityDpi = 500;
            else if(configuration.densityDpi >= 300)
                configuration.densityDpi = 400;
            else if(configuration.densityDpi >= 100)
                configuration.densityDpi = 200;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.densityDpi * metrics.density;
            this.getResources().updateConfiguration(configuration, metrics);
        }
    }
}
