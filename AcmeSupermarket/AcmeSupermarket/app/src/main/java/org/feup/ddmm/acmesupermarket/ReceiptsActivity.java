package org.feup.ddmm.acmesupermarket;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReceiptsActivity extends AppCompatActivity {

    private int noVouchers;
    private float accumAmount;
    TextView vouchersTextView;
    TextView accumAmountTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        vouchersTextView = (TextView) findViewById(R.id.no_vouchers);
        accumAmountTextView = (TextView) findViewById(R.id.accumulated_amount);

        noVouchers = 0; //TEMP VALUE - GET FROM SERVER
        accumAmount = 0.00F; //TEMP VALUE - GET FROM SERVER

        vouchersTextView.setText(Integer.toString(noVouchers));
        accumAmountTextView.setText(accumAmount + "€");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
