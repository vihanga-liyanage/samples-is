package com.vihanga.sample.push.auth;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.json.JSONObject;

/**
 Custom application class to override FCM initialization
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(ConfigActivity.PUSH_CONFIG_PARAM_NAME, MODE_PRIVATE);
        String json = prefs.getString(ConfigActivity.GOOGLE_SERVICES_JSON_PARAM_NAME, null);

        if (json != null && !json.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                String projectId = jsonObject.getJSONObject("project_info").getString("project_id");
                String projectNumber = jsonObject.getJSONObject("project_info").getString("project_number");
                String storageBucket = jsonObject.getJSONObject("project_info").getString("storage_bucket");

                JSONObject client = jsonObject.getJSONArray("client").getJSONObject(0);
                String appId = client.getJSONObject("client_info").getString("mobilesdk_app_id");
                String apiKey = client.getJSONArray("api_key").getJSONObject(0).getString("current_key");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApplicationId(appId)           // Required
                        .setApiKey(apiKey)                 // Required
                        .setProjectId(projectId)           // Required
                        .setStorageBucket(storageBucket)   // Optional
                        .setGcmSenderId(projectNumber)     // Optional but useful
                        .build();

                if (FirebaseApp.getApps(this).isEmpty()) {
                    FirebaseApp.initializeApp(this, options);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
