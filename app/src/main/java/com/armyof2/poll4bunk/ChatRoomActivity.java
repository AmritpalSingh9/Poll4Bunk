package com.armyof2.poll4bunk;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.armyof2.poll4bunk.LaunchActivity.SERVER_ID;
import static com.armyof2.poll4bunk.LaunchActivity.name2;
import static com.armyof2.poll4bunk.MainActivity.servTitle;

public class ChatRoomActivity extends AppCompatActivity{

    private FloatingActionButton fab;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());
        adjustDisplayScale( getResources().getConfiguration());
        setContentView(R.layout.activity_chatroom);
        displayChatMessages();
        setTitle(servTitle);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                String[] strArray = name2.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(SERVER_ID)
                        .child("Zmessages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(), builder.toString())
                        );

                // Clear the input
                input.setText("");

            }
        });

    }

    public void displayChatMessages(){
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(SERVER_ID).child("Zmessages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
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
            android.util.Log.d("TAG", "adjustDisplayScale: " + configuration.densityDpi);
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
