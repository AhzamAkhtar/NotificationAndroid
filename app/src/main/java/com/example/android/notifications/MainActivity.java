package com.example.android.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private EditText mTitle , mMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        mTitle = findViewById(R.id.mTitle);
        mMessage = findViewById(R.id.mMessage);

        findViewById(R.id.mSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mTitle.getText().toString().isEmpty() && !mMessage.getText().toString().isEmpty()){
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            "/topics/all",
                            mTitle.getText().toString(),
                            mMessage.getText().toString(),
                            getApplicationContext(),
                            MainActivity.this
                    );

                    notificationsSender.SendNotifications();
                } else {
                    Toast.makeText(MainActivity.this,"Chuuuuuuup",Toast.LENGTH_LONG).show();
                }
            }
        });




//        findViewById(R.id.mSend).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String title = mTitle.getText().toString().trim();
//                String message = mMessage.getText().toString().trim();
//                if(!title.equals("") && !message.equals("")){
//                    FCMSend.pushNotification(
//                            MainActivity.this,
//                            "fCN4NIgrSGWT30HpNlXYcz:APA91bHDFQAU2kr8RpWFApN7bhu30z9cPrMkw1qNIWMDED9V6pVkmwolBJJ_NxAhkpzT8GRcDt4O6CSC_l9k6jVbPLWI2wsj7s0ME-CWCNTrWA4u8MHMlt9ZygDjVjQyBXahaQY7AdA_",
//                            title,
//                            message
//                    );
//                }
//            }
//        });



//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//                        System.out.println(token);
//                        // Log and toast
//
//                    }
//                });
    }
}