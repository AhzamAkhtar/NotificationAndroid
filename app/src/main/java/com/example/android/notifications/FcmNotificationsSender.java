package com.example.android.notifications;


import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//private static String SERVER_KEY = "key=AAAA5wgzS3A:APA91bEc0DDyg4AqIqxmC5TFYN_AfmKQzJVYmkCCxc2P3dofiMJBusjmJ1Ag9wrAogU7QOb_Ss6-lAprTtYcZ5jvEf_Q4RGaXBCIf9fzcaqUis1aybwzqclfWItGYqA-6TUpVRX6J3YK";

public class FcmNotificationsSender {
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA5wgzS3A:APA91bEc0DDyg4AqIqxmC5TFYN_AfmKQzJVYmkCCxc2P3dofiMJBusjmJ1Ag9wrAogU7QOb_Ss6-lAprTtYcZ5jvEf_Q4RGaXBCIf9fzcaqUis1aybwzqclfWItGYqA-6TUpVRX6J3YK";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.notifications);

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "key=" + fcmServerKey);
                    return header;
                }


            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

