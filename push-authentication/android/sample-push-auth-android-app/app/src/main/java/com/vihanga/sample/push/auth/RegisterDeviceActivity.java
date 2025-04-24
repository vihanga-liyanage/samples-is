package com.vihanga.sample.push.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vihanga.sample.push.auth.util.WSO2ISPushAuthFunctions;

public class RegisterDeviceActivity extends AppCompatActivity {

    private EditText etDeviceId, etChallenge, etDeviceName, etDeviceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_device);

        etDeviceId = findViewById(R.id.etDeviceId);
        etChallenge = findViewById(R.id.etChallenge);
        etDeviceName = findViewById(R.id.etDeviceName);
        etDeviceModel = findViewById(R.id.etDeviceModel);
    }

    public void register(View view) {
        Log.i("INFO===", "Registering device...");

        // Get FCM token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                return;
            }

            String deviceToken = task.getResult();
            String deviceId = etDeviceId.getText().toString();
            String deviceName = etDeviceName.getText().toString();
            String deviceModel = etDeviceModel.getText().toString();
            String challenge = etChallenge.getText().toString();

            try {
                WSO2ISPushAuthFunctions.invokeDeviceRegistrationAPI(this, deviceId, deviceName,
                        deviceModel, deviceToken, challenge,
                        new WSO2ISPushAuthFunctions.PushAuthAPICallback() {
                                @Override
                                public void onSuccess(String responseBody) {
                                    showAlert("Device Registration Success", "Success");
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    showAlert("Device Registration Failed", errorMessage);
                                }
                            });
            } catch (Exception e) {
                Log.e("ERROR===", String.valueOf(e));
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(RegisterDeviceActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    goToMainActivity();
                })
                .setCancelable(true)
                .show();
    }

    public void cancel(View view) {
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegisterDeviceActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
