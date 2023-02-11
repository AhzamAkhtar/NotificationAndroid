package com.example.android.notifications;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText mTitle, mMessage;
    List<String> list = new ArrayList<>();
    HashMap<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCentralLocation();

        mTitle = findViewById(R.id.mTitle);
        mMessage = findViewById(R.id.mMessage);


        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getString("deviceId"));
                                map.put(document.getString("deviceId") + "lat", document.getString("Lat"));
                                map.put(document.getString("deviceId") + "lng", document.getString("Lng"));
                                Log.d("xx", map.toString());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        System.out.println(token);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//                    }
//                });

        findViewById(R.id.mSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mTitle.getText().toString().isEmpty() && !mMessage.getText().toString().isEmpty()){
                    for(String i : list){
                        Double distance = getDistance(
                                map.get("centralLat"),
                                map.get("centralLng"),
                                map.get(i+"lat"),
                                map.get(i+"lng")
                        );
                        String newdistance = (String) String.valueOf(distance).subSequence(0,1);
                        Log.d("cccccccDistace",newdistance);
                        if(Integer.parseInt(newdistance)<3) {
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                    i,
                                    mTitle.getText().toString(),
                                    mMessage.getText().toString(),
                                    getApplicationContext(),
                                    MainActivity.this
                            );
                            notificationsSender.SendNotifications();
                        }
                    }



                } else {
                    Toast.makeText(MainActivity.this,"Chuuuuuuup",Toast.LENGTH_LONG).show();
                }
            }
        });




    }



    public void getCentralLocation(){
        DocumentReference docRef = db.collection("location").document("bEnlU8XjJHrfz9GoqZY8");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Tag", "DocumentSnapshot data: " + document.getData());
                        String lat = document.getString("Lat");
                        String Lng = document.getString("Lng");
                        map.put("centralLat",lat);
                        map.put("centralLng",Lng);
                    } else {
                        Log.d("Tag", "No such document");
                    }
                } else {
                    Log.d("Tag", "get failed with ", task.getException());
                }
            }
        });

    }

    public double getDistance(
            String centralLat,
            String centralLng,
            String userLat,
            String userLng
    ) {
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(Double.parseDouble(centralLat));
        startPoint.setLongitude(Double.parseDouble(centralLng));

        Location endPoint=new Location("locationb");
        endPoint.setLatitude(Double.parseDouble(userLat));
        endPoint.setLongitude(Double.parseDouble(userLng));

        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }
}