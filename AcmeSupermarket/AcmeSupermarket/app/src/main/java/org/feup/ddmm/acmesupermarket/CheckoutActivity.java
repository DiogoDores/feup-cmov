package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private boolean hasAppliedVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        this.pref = getSharedPreferences("pref", MODE_PRIVATE);

        // Basket button listener.
        findViewById(R.id.go_back_checkout_button).setOnClickListener(v -> openBasketActivity());

        // Apply voucher button listener.
        findViewById(R.id.voucher_checkout_button).setOnClickListener(v -> {
            String voucher = this.pref.getString("voucher", null), msg = "";
            Resources res = getResources();

            if (voucher == null) {
                msg = res.getString(R.string.voucher_not_found);
            } else {
                if (!this.hasAppliedVoucher) {
                    msg = String.format("%s %s!", res.getString(R.string.voucher_applied), voucher);
                } else {
                    msg = String.format("%s %s!", res.getString(R.string.voucher_removed), voucher);
                }
                this.hasAppliedVoucher = !this.hasAppliedVoucher;
            }

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        });

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

