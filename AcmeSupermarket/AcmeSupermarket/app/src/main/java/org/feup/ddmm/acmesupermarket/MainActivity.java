package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find both login and register buttons on the view.
        this.login = findViewById(R.id.button_main_login);
        this.register = findViewById(R.id.button_main_register);

        this.login.setOnClickListener(v ->
            startActivity(new Intent(this, LoginActivity.class)));

        this.register.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class)));
    }
}
