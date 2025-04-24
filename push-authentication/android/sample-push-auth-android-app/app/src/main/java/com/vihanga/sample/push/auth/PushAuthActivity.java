package com.vihanga.sample.push.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static com.example.fcm.R.id.textView;

import com.vihanga.sample.push.auth.util.WSO2ISPushAuthFunctions;

import com.example.fcm.R;

public class PushAuthActivity extends AppCompatActivity {

	private String deviceId, pushAuthId, challenge, numberChallenge, username,
			browser, deviceOS, ipAddress, applicationName;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		mTextView = findViewById(textView);

		handleIntentData(getIntent());
		showPushPrompt();
	}

	public void approve(View view) {
		Log.i("INFO===", "Approving push auth request...");
        try {
            WSO2ISPushAuthFunctions.invokePushAuthAPI(this, deviceId, pushAuthId, challenge,
					"APPROVED", numberChallenge,
					new WSO2ISPushAuthFunctions.PushAuthAPICallback() {
						@Override
						public void onSuccess(String responseBody) {
							showAlert("Push Auth Request", "Request approved successfully.");
						}

						@Override
						public void onError(String errorMessage) {
							showAlert("Login Approval Failed", errorMessage);
						}
					});
        } catch (Exception e) {
            Log.e("ERROR===", String.valueOf(e));
		}
	}

	private void showAlert(String title, String message) {
		new AlertDialog.Builder(PushAuthActivity.this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("OK", (dialog, which) -> {
					goToMainActivity();
				})
				.setCancelable(true)
				.show();
	}

	public void deny(View view) {
		Log.i("INFO===", "Denying push auth request...");
        try {
            WSO2ISPushAuthFunctions.invokePushAuthAPI(this, deviceId, pushAuthId, challenge,
					"DENIED", numberChallenge,
					new WSO2ISPushAuthFunctions.PushAuthAPICallback() {
						@Override
						public void onSuccess(String responseBody) {
							showAlert("Push Auth Request", "Request denied successfully.");
						}

						@Override
						public void onError(String errorMessage) {
							showAlert("Login Denying Failed", errorMessage);
						}
					});
        } catch (Exception e) {
			Log.e("ERROR===", String.valueOf(e));
        }
	}

	private void showPushPrompt() {
		String message = "<b>Are you trying to sign in?</b><br><br>" +
				"Username: " + username + "<br>" +
				"Browser: " + browser + "<br>" +
				"OS: " + deviceOS + "<br>" +
				"IP: " + ipAddress + "<br>" +
				"Application: " + applicationName + "<br>" +
				"Number Challenge: " + numberChallenge + "<br><br>";

		mTextView.setText(Html.fromHtml(message));
	}

	private void handleIntentData(Intent intent) {
		if (intent != null && intent.getExtras() != null) {
			deviceId = intent.getStringExtra("deviceId");
			pushAuthId = intent.getStringExtra("pushId");
			challenge = intent.getStringExtra("challenge");
			numberChallenge = intent.getStringExtra("numberChallenge");
			username = intent.getStringExtra("username");
			browser = intent.getStringExtra("browser");
			deviceOS = intent.getStringExtra("deviceOS");
			ipAddress = intent.getStringExtra("ipAddress");
			applicationName = intent.getStringExtra("applicationName");
		} else {
			goToMainActivity();
		}
	}

	public void cancel(View view) {
		goToMainActivity();
	}

	private void goToMainActivity() {
		Intent intent = new Intent(PushAuthActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
}
