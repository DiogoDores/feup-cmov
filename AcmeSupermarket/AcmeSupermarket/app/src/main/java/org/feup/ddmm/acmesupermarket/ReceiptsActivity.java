package org.feup.ddmm.acmesupermarket;

import android.os.Bundle;

import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReceiptsActivity extends AppCompatActivity {

    private int noVouchers;
    private float accumAmount;
    TextView vouchersTextView;
    TextView accumAmountTextView;

    ReceiptAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<Receipt> listData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        vouchersTextView = (TextView) findViewById(R.id.no_vouchers);
        accumAmountTextView = (TextView) findViewById(R.id.accumulated_amount);

        noVouchers = 0; //TEMP VALUE - GET FROM SERVER
        accumAmount = 0.00F; //TEMP VALUE - GET FROM SERVER

        vouchersTextView.setText(Integer.toString(noVouchers));
        accumAmountTextView.setText(accumAmount + "€");

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.receipts_view);

        // preparing list data
        prepareListData();

        listAdapter = new ReceiptAdapter(this, listData);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listData = new ArrayList<Receipt>();

        // Adding child data
        Product p1 = new Product("id1", 10.5F, "poopoo");
        Product p2 = new Product("id1", 10.5F, "peepee");
        ArrayList<Product> products = new ArrayList<Product>();
        products.add(p1);
        products.add(p2);

        Date date = new Date();

        Receipt r1 = new Receipt(date, products, 10.99F, false);
        listData.add(r1);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
