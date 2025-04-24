package com.vihanga.sample.push.auth;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		Map<String, String> data = remoteMessage.getData();

		Intent intent = new Intent(this, PushAuthActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		for (Map.Entry<String, String> entry : data.entrySet()) {
			Log.i(entry.getKey(), entry.getValue());
			intent.putExtra(entry.getKey(), entry.getValue());
		}

		startActivity(intent);
	}

	@Override
	public void onNewToken(@NonNull String var1) {
		Log.i("New FCM Token Issued", var1);
	}
}