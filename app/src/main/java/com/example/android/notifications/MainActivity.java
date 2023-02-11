package com.example.android.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
    int PERMISSION_ID = 44;
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
        //getCentralLocation();

        mTitle = findViewById(R.id.mTitle);
        mMessage = findViewById(R.id.mMessage);

        getLastLocation();

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
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                i,
                                mTitle.getText().toString(),
                                mMessage.getText().toString(),
                                getApplicationContext(),
                                MainActivity.this
                        );
                        notificationsSender.SendNotifications();
                    }



                } else {
                    Toast.makeText(MainActivity.this,"Chuuuuuuup",Toast.LENGTH_LONG).show();
                }
            }
        });




    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Log.d("zz", String.valueOf(location.getLatitude()));
                            Log.d("zz", String.valueOf(location.getLongitude()));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


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
        distance = (distance/1000).toString().subSequence(0,3);
        distance = (distance/1000).toString().subSequence(0,3);
        return distance;
    }
}