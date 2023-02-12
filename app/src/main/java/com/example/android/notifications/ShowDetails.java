package com.example.android.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        String msg = getIntent().getStringExtra("TItle");
        String description = getIntent().getStringExtra("Description");


        TextView title = findViewById(R.id.tvMsgTitle);
        TextView body = findViewById(R.id.tvMsgMessage);

        title.setText("Notification Title  = "+msg);
        body.setText("Notification Description  = "+description);

    }
}