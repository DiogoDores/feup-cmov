package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register button listener
        Button btnRegister = findViewById(R.id.button_register_confirm);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the credentials inputted by the user
                String name = ((EditText) findViewById(R.id.edit_register_name)).getText().toString();
                String username = ((EditText) findViewById(R.id.edit_register_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.edit_register_password)).getText().toString();

                registerUser(name, username, password);
            }
        });

        this.mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "http://10.0.2.2:3000/users", null, new Response.Listener<JSONObject>() {
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
            payload.put("name", name);
            payload.put("username", username);
            // payload.put("password", password);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/users", payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
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
}
