package com.vihanga.sample.push.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm.R;

public class ConfigActivity extends AppCompatActivity {
    public static final String DEFAULT_BASE_URL = "https://10.0.2.2:9443";
    public static final int DEFAULT_EXPIRATION_MINUTES = 15;
    public static final int DEFAULT_NOT_BEFORE_MINUTES = 0;
    public static final String DEFAULT_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEAwdysF2GMCgENwNmGvdk0c31JlkLHqs3X4cjN2On1/e4GY2ZU\n+Q/ZGkmWPhYxx9e18rHrpR7MYM92Nji8cv9q0MgwIHj0CpfMq73GJh/HKZD2638x\npdOAMpgoiFnhGGk/hqtrtfS7GZuIKOsdwNYTX8K1CTKGAJvJLu6X9d6NrQJmxl2C\nVYXuLyxrJ/3/DVPCIcPLxUk6pzz/Bsaye4LsiwGhSDMhEIvYO1ZSVN48L1yzVG/y\nQzGtStEj8bNBQCkHJewU3pMle1lVSdcefwo6j4L54gNHG65QibswFpUyhkoI1oUv\nVLm2vjlVGXuWK0akC0/DITE/nOhjPUk/9JEuNQIDAQABAoIBAELDx4bT4wFNBJoO\nsF9wzyYZF39G53H1K2zIldAnHz+BOrT/+LLAGQJ7JmGijilnqnN3gBLhbPzIZktd\nNdWbDoPx9dUxIscFKYlaNcRKfHJdyRQovYYBNaz3BzqSTbn1AVpVbiZ/rvIuRPHm\nq1wOviRkL6oHuuu5u0tNA4u7Rmq3B9/hHwQy0OaM1C3n9mNTHIXd9kOp74F9Zu3M\n+iX6L59n71cSF4KPg0YREJfV/mV9YBklEJm0Gzv8WdEAdXttoPgy/BFoa9E7bj20\nYgwujkIYG5+yi1M4L6dRMqTjoFch+WfB0mL+c0itYXSBP86Rq6IXOcTOP7K+ENA6\nqzHFGY8CgYEA6i5samqn8XgXBnN65PLY1DrxeLYEEVIi7uvh/cOl9hFoA7Gy5yTZ\n5lnNlm2WneH4475c8WNbSU0YsGcP//yWkzKNztEDD5tmzlLtPgoP1QdT9nh4W5vQ\nRFXpohS8gCqpBsi6lEdQSeI2zNtFbbDv2KL8hhcEV3ZUB1GtOEj55fsCgYEA0+yS\nak88i9meOq3xf5FempIJ3DhMZsv/dHbs5UZc/LfFmolpp38PEnpJnNUernzkzh9z\nF5KjzJrtu9ayFcjmGmTwSPDYKyJTM/0jmanjc7JcfNDuUT9qO8cDoB6p/hB+j6ui\nFAZuuX3mxrelGUOrce/d0z6iEQLfBMqmLqA6dY8CgYEA17ff4r9Q6RxoiWbDJDeY\nCRgq67kvUg1JZLEhQjOBo4QxPoXoeQf3OTwsJ4/XmKRaMuBemXWe140PWx/1GyfA\nROPiwUrD+RJ7xz+YjveR0fioHXin1itX1DsXtlhHtACYJ0mspX5ztTuOo3KxJ/Q/\nXF33JEX7l93GVd+lIx4GJRUCgYEAww+DDLr1/btGOriUClkhfKBi1wIywIOQFADK\nt5X2TN5R0ZYdW4BFvTo1u1cmsHBgoaCs7MuaXN9VZomMzSRnN3AlAt47+ifT8YxU\nUkHfC0TmgDRGxx3ZiD/8BEt1KFbCRzcxLcFl9PH/knTCT+jLS0n/IUsLHSFJaT2l\njFEKvd0CgYEApKww0BeoUHa31sjoIV2Z1besYE8A6gOX1yqoXIf+Xuwg2WJTSDDG\nAaVps2SMSsCsWzrmI2YQPgYTmOB1LkISMv2N8WsIE8WVamSI6wq2Ajd0B68myPII\nAVNY5K5kWs6Bhe/OuFsgZLYQCw+vN7Ww6hfeLhkZyseRlI+stnF1VpA=\n-----END RSA PRIVATE KEY-----\n";
    public static final String DEFAULT_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwdysF2GMCgENwNmGvdk0\nc31JlkLHqs3X4cjN2On1/e4GY2ZU+Q/ZGkmWPhYxx9e18rHrpR7MYM92Nji8cv9q\n0MgwIHj0CpfMq73GJh/HKZD2638xpdOAMpgoiFnhGGk/hqtrtfS7GZuIKOsdwNYT\nX8K1CTKGAJvJLu6X9d6NrQJmxl2CVYXuLyxrJ/3/DVPCIcPLxUk6pzz/Bsaye4Ls\niwGhSDMhEIvYO1ZSVN48L1yzVG/yQzGtStEj8bNBQCkHJewU3pMle1lVSdcefwo6\nj4L54gNHG65QibswFpUyhkoI1oUvVLm2vjlVGXuWK0akC0/DITE/nOhjPUk/9JEu\nNQIDAQAB\n-----END PUBLIC KEY-----";
    public static final String BASE_URL_PARAM_NAME = "base_url";
    public static final String PRIVATE_KEY_PARAM_NAME = "private_key";
    public static final String PUBLIC_KEY_PARAM_NAME = "public_key";
    public static final String EXP_MINUTES_PARAM_NAME = "exp_minutes";
    public static final String NOT_BEFORE_MINUTES_PARAM_NAME = "not_before_minutes";
    public static final String PUSH_CONFIG_PARAM_NAME = "push_config";
    public static final String GOOGLE_SERVICES_JSON_PARAM_NAME = "firebase_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        EditText inputUrl = findViewById(R.id.inputUrl);
        EditText inputPrivateKey = findViewById(R.id.inputPrivateKey);
        EditText inputPublicKey = findViewById(R.id.inputPublicKey);
        EditText inputExpMinutes = findViewById(R.id.inputExpMinutes);
        EditText inputGoogleServicesJson = findViewById(R.id.inputGoogleServicesJson);
        Button saveBtn = findViewById(R.id.saveBtn);

        SharedPreferences prefs = getSharedPreferences(PUSH_CONFIG_PARAM_NAME, MODE_PRIVATE);

        // Load saved values or defaults
        inputUrl.setText(prefs.getString(BASE_URL_PARAM_NAME, DEFAULT_BASE_URL));
        inputPrivateKey.setText(prefs.getString(PRIVATE_KEY_PARAM_NAME, DEFAULT_PRIVATE_KEY));
        inputPublicKey.setText(prefs.getString(PUBLIC_KEY_PARAM_NAME, DEFAULT_PUBLIC_KEY));
        inputExpMinutes.setText(String.valueOf(prefs.getInt(EXP_MINUTES_PARAM_NAME, DEFAULT_EXPIRATION_MINUTES)));
        inputGoogleServicesJson.setText(prefs.getString(GOOGLE_SERVICES_JSON_PARAM_NAME, null));

        saveBtn.setOnClickListener(v -> {
            if (inputGoogleServicesJson.getText().toString().isEmpty()) {
                inputGoogleServicesJson.setError("Google Services JSON is mandatory!");
                inputGoogleServicesJson.requestFocus();
                return;
            }

            prefs.edit()
                    .putString(BASE_URL_PARAM_NAME, inputUrl.getText().toString())
                    .putString(PRIVATE_KEY_PARAM_NAME, inputPrivateKey.getText().toString())
                    .putString(PUBLIC_KEY_PARAM_NAME, inputPublicKey.getText().toString())
                    .putInt(EXP_MINUTES_PARAM_NAME, Integer.parseInt(inputExpMinutes.getText().toString()))
                    .putString(GOOGLE_SERVICES_JSON_PARAM_NAME, inputGoogleServicesJson.getText().toString())
                    .commit();

            // Restart app natively
            Intent intent = getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity(); // Close all activities
                Runtime.getRuntime().exit(0); // Kill the process to ensure clean restart
            }
        });
    }
}
