package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Basket button listener.
        findViewById(R.id.basket_button).setOnClickListener(v -> openBasketActivity());

        // Receipts button listener.
        findViewById(R.id.receipts_button).setOnClickListener(v -> openReceiptsActivity());
    }

    public void openBasketActivity() {
        Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void openReceiptsActivity() {
        startActivity(new Intent(getApplicationContext(), ReceiptsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

