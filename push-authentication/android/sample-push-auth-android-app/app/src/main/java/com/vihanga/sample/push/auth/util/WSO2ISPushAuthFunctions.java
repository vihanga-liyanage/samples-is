package com.vihanga.sample.push.auth.util;

import static android.content.Context.MODE_PRIVATE;

import static com.vihanga.sample.push.auth.ConfigActivity.BASE_URL_PARAM_NAME;
import static com.vihanga.sample.push.auth.ConfigActivity.DEFAULT_EXPIRATION_MINUTES;
import static com.vihanga.sample.push.auth.ConfigActivity.DEFAULT_NOT_BEFORE_MINUTES;
import static com.vihanga.sample.push.auth.ConfigActivity.DEFAULT_PRIVATE_KEY;
import static com.vihanga.sample.push.auth.ConfigActivity.DEFAULT_PUBLIC_KEY;
import static com.vihanga.sample.push.auth.ConfigActivity.DEFAULT_BASE_URL;
import static com.vihanga.sample.push.auth.ConfigActivity.EXP_MINUTES_PARAM_NAME;
import static com.vihanga.sample.push.auth.ConfigActivity.NOT_BEFORE_MINUTES_PARAM_NAME;
import static com.vihanga.sample.push.auth.ConfigActivity.PRIVATE_KEY_PARAM_NAME;
import static com.vihanga.sample.push.auth.ConfigActivity.PUBLIC_KEY_PARAM_NAME;
import static com.vihanga.sample.push.auth.ConfigActivity.PUSH_CONFIG_PARAM_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WSO2ISPushAuthFunctions {

    public interface PushAuthAPICallback {
        void onSuccess(String responseBody);
        void onError(String errorMessage);
    }

    public static void invokePushAuthAPI(Context context, String deviceId, String pushAuthId,
                                         String challenge, String status, String numberChallenge,
                                         PushAuthAPICallback callback) throws Exception {

        SharedPreferences prefs = context.getSharedPreferences(PUSH_CONFIG_PARAM_NAME, MODE_PRIVATE);
        String url = prefs.getString(BASE_URL_PARAM_NAME, DEFAULT_BASE_URL);
        String privateKey = prefs.getString(PRIVATE_KEY_PARAM_NAME, DEFAULT_PRIVATE_KEY);
        int expirationMinutes = prefs.getInt(EXP_MINUTES_PARAM_NAME, DEFAULT_EXPIRATION_MINUTES);
        int notBeforeMinutes = prefs.getInt(NOT_BEFORE_MINUTES_PARAM_NAME, DEFAULT_NOT_BEFORE_MINUTES);

        String jwt = generateJWT(deviceId, privateKey, pushAuthId, challenge, status,
                numberChallenge, expirationMinutes, notBeforeMinutes);
        String jsonBody = "{\"authResponse\":\"" + jwt + "\"}";
        Log.i("Push Auth body", jsonBody);

        String pushAuthUrl = url + "/push-auth/authenticate";
        Log.i("Push Auth URL", pushAuthUrl);

        RequestBody body = RequestBody.create(
                Objects.requireNonNull(MediaType.parse("application/json")),
                jsonBody
        );

        assert url != null;
        Request request = new Request.Builder()
                .url(pushAuthUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = getUnsafeOkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Auth API", "Request failed", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Push Auth request failed: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("Auth API", "Response code: " + response.code());
                String bodyStr = response.body() != null ? response.body().string() : "";
                Log.i("Auth API", "Response body: " + bodyStr);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.isSuccessful()) {
                        callback.onSuccess(bodyStr);
                    } else {
                        callback.onError("Server responded with error code: " + response.code());
                    }
                });
            }
        });
    }

    public static void invokeDeviceRegistrationAPI(Context context, String deviceId,
                                                   String deviceName, String deviceModel,
                                                   String deviceToken, String challenge,
                                                   PushAuthAPICallback callback) throws Exception {

        SharedPreferences prefs = context.getSharedPreferences(PUSH_CONFIG_PARAM_NAME, MODE_PRIVATE);
        String url = prefs.getString(BASE_URL_PARAM_NAME, DEFAULT_BASE_URL);
        String privateKeyPem = prefs.getString(PRIVATE_KEY_PARAM_NAME, DEFAULT_PRIVATE_KEY);
        String publicKeyPem = prefs.getString(PUBLIC_KEY_PARAM_NAME, DEFAULT_PUBLIC_KEY);

        String payload = generateDeviceRegistrationPayload(deviceId, deviceName, deviceModel,
                deviceToken, publicKeyPem, privateKeyPem, challenge);

        Log.i("Registration Payload", payload);

        String registrationUrl = url + "/api/users/v1/me/push/devices";
        Log.i("Registration URL", registrationUrl);

        OkHttpClient client = getUnsafeOkHttpClient();

        RequestBody body = RequestBody.create(
                Objects.requireNonNull(MediaType.parse("application/json")),
                payload
        );

        Request request = new Request.Builder()
                .url(registrationUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Device Register API", "Request failed", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Push Auth request failed: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("DeviceRegisterAPI",  "Response code: " + response.code());
                String bodyStr = response.body() != null ? response.body().string() : "";
                Log.i("DeviceRegisterAPI", "Response body: " + bodyStr);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.isSuccessful()) {
                        callback.onSuccess(bodyStr);
                    } else {
                        callback.onError("Server responded with error code: " + response.code());
                    }
                });
            }
        });
    }

    /**
     * WARNING: This OkHttpClient bypasses all SSL certificate checks and hostname verification.
     * It should ONLY be used in development or testing environments to connect to servers
     * with self-signed or untrusted certificates (e.g., localhost:9443).
     *
     * DO NOT use this in production — it exposes the app to man-in-the-middle (MITM) attacks.
     */
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateJWT(String deviceId, String privateKeyPem, String pushAuthId,
            String challenge, String status, String number, int expirationMinutes,
            int notBeforeMinutes
    ) throws Exception {

        RSAPrivateKey privateKey = getPrivateKeyFromPem(privateKeyPem);

        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expirationMinutes * 60 * 1000L;
        long nbfMillis = nowMillis + notBeforeMinutes * 60 * 1000L;

        Date exp = new Date(expMillis);
        Date nbf = new Date(nbfMillis);

        Map<String, Object> header = new HashMap<>();
        header.put("deviceId", deviceId);

        Algorithm algorithm = Algorithm.RSA256(null, privateKey);

        return JWT.create()
                .withHeader(header)
                .withClaim("pushAuthId", pushAuthId)
                .withClaim("challenge", challenge)
                .withClaim("response", status)
                .withClaim("numberChallenge", number)
                .withNotBefore(nbf)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    private static String generateDeviceRegistrationPayload(
            String deviceId, String deviceName, String deviceModel, String deviceToken,
            String publicKeyPem, String privateKeyPem, String challenge
    ) throws Exception {

        RSAPublicKey publicKey = getPublicKeyFromPem(publicKeyPem);
        RSAPrivateKey privateKey = getPrivateKeyFromPem(privateKeyPem);

        // Create the string to sign: challenge.deviceToken
        String toSign = challenge + "." + deviceToken;

        // Sign it with the private key
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(toSign.getBytes());
        String signed = Base64.encodeToString(signature.sign(), Base64.NO_WRAP);

        // Prepare the JSON payload
        JSONObject payload = new JSONObject();
        payload.put("deviceId", deviceId);
        payload.put("name", deviceName);
        payload.put("model", deviceModel);
        payload.put("deviceToken", deviceToken);
        payload.put("publicKey", Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP));
        payload.put("signature", signed);

        return payload.toString();
    }

    public static RSAPublicKey getPublicKeyFromPem(String pem) throws Exception {
        String sanitized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.decode(sanitized, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static RSAPrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        String sanitized = pem
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.decode(sanitized, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}
