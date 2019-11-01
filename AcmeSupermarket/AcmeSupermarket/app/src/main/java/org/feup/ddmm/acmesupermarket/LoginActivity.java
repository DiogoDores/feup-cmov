package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Register request queue.
        this.mQueue = Volley.newRequestQueue(this);

        // Get shared preferences.
        this.pref = getSharedPreferences("pref", MODE_PRIVATE);

        // Listener for login button.
        findViewById(R.id.button_login_confirm).setOnClickListener(v -> handleLogin());
    }

    private void openBasketActivity() {
        startActivity(new Intent(this, BasketActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void saveLoginInfo(String uuid, String smpk) {
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString("uuid", uuid);
        editor.putString("sm_public_key", smpk);
        editor.apply();

        Toast.makeText(this, "UUID " + this.pref.getString("uuid", null) + " successfully logged in!", Toast.LENGTH_SHORT).show();
        this.openBasketActivity();
    }

    private void sendLoginRequest(String username) {
        String url = getString(R.string.ip) + "/login/" + username;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, res -> {
            try {
                this.saveLoginInfo(res.getString("uuid"), res.getString("sm_public_key"));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }, err -> {
            Toast.makeText(LoginActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
        });

        this.mQueue.add(req);
    }

    private void handleLogin() {
        String pwd = this.pref.getString("password", null);
        String pwdIn = ((EditText) findViewById(R.id.edit_login_password)).getText().toString();

        if (pwd != null && pwd.equals(pwdIn)) {
            String username = ((EditText) findViewById(R.id.edit_login_username)).getText().toString();
            this.sendLoginRequest(username);
        } else {
            Toast.makeText(this, "Invalid credentials, have you registered?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
