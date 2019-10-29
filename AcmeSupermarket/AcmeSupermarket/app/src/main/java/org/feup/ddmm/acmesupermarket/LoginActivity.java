package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Listener for login button.
        findViewById(R.id.button_login_confirm).setOnClickListener(v ->
            Toast.makeText(getApplicationContext(), "Login button", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        // TODO: Login here eventually.
    }
}
