package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Basket button listener.
        findViewById(R.id.go_back_checkout_button).setOnClickListener(v -> openBasketActivity());

    }

    public void openBasketActivity() {
        startActivity(new Intent(getApplicationContext(), BasketActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

