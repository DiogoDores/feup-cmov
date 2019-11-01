package org.feup.ddmm.acmesupermarket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BasketActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
