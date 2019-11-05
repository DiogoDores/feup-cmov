package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BasketActivity extends AppCompatActivity {
    private ArrayList<Product> basket = new ArrayList<Product>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        findViewById(R.id.button_camera_open).setOnClickListener(v -> {
            startActivity(new Intent(this, ScanActivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
