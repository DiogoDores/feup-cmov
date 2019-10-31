package org.feup.ddmm.acmesupermarket;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.x500.X500Principal;

public class RegisterActivity extends AppCompatActivity {
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Register button listener.
        findViewById(R.id.button_register_confirm).setOnClickListener(v -> {
            String name = ((EditText) findViewById(R.id.edit_register_name)).getText().toString();
            String username = ((EditText) findViewById(R.id.edit_register_username)).getText().toString();
            String password = ((EditText) findViewById(R.id.edit_register_password)).getText().toString();

            registerUser(name, username, password);
        });

        this.mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, getString(R.string.ip), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response:", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("Response:", error.toString());
            }
        });
        this.mQueue.add(req);
    }

    private void registerUser(String name, String username, String password) {
        try {
            JSONObject payload = new JSONObject();

            getCryptographicKeyPair();  // Generate a cryptographic key pair.
            String publicKey = encodeKeyToString(getPublicKey());    // Get public key from generated key pair.
            payload.put("public_key", publicKey);

            payload.put("name", name);
            payload.put("username", username);
            payload.put("password", password);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, getString(R.string.ip), payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject res) {
                    //Toast.makeText(RegisterActivity.this, res.toString(), Toast.LENGTH_SHORT).show();

                    // Parse response JSON.
                    try {
                        // Save response parameters in shared preferences.
                        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("uuid", res.getString("uuid"));
                        editor.putString("sm_public_key", res.getString("sm_public_key"));
                        editor.apply();

                        Toast.makeText(RegisterActivity.this, pref.getString("uuid", "No UUID found."), Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            this.mQueue.add(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private KeyPair getCryptographicKeyPair() {
        try {
            KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
            ks.load(null);

            if (ks.getEntry(Constants.keyName, null) == null) {
                Calendar start = new GregorianCalendar(), end = new GregorianCalendar();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, Constants.ANDROID_KEYSTORE);

                AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(this)
                    .setKeySize(Constants.KEY_SIZE)
                    .setAlias(Constants.keyName)
                    .setSubject(new X500Principal("CN=" + Constants.keyName))
                    .setSerialNumber(BigInteger.valueOf(12121212))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();

                kpg.initialize(spec);
                return kpg.generateKeyPair();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @TargetApi(26)
    private String encodeKeyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private PublicKey getPublicKey() {
        try {
            KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(Constants.keyName, null);

            PublicKey publicKey = ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PrivateKey getPrivateKey() {
        try {
            KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(Constants.keyName, null);

            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
