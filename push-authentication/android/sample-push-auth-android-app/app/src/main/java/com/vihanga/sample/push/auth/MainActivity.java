package com.vihanga.sample.push.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm.R;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = getSharedPreferences(ConfigActivity.PUSH_CONFIG_PARAM_NAME, MODE_PRIVATE);

		// Check if google-services.json is available
		if (!prefs.contains(ConfigActivity.GOOGLE_SERVICES_JSON_PARAM_NAME)) {
			// Launch ConfigActivity and return early
			startActivity(new Intent(this, ConfigActivity.class));
			finish();
			return;
		}

		setContentView(R.layout.activity_main);
	}

	public void registerDevice(View view) {
		Intent intent = new Intent(this, RegisterDeviceActivity.class);
		startActivity(intent);
	}

	public void openConfig(View view) {
		Intent intent = new Intent(this, ConfigActivity.class);
		startActivity(intent);
	}
}