package org.feup.ddmm.acmesupermarket;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class ReceiptsActivity extends AppCompatActivity {

    private int noVouchers;
    private float accumAmount;
    TextView vouchersTextView;
    TextView accumAmountTextView;

    ReceiptAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<Receipt> receipts;

    private SharedPreferences pref;
    private RequestQueue mQueue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        // Register request queue.
        this.mQueue = Volley.newRequestQueue(this);

        this.pref = getSharedPreferences("pref", MODE_PRIVATE);
        this.receipts = new ArrayList<Receipt>();

        vouchersTextView = (TextView) findViewById(R.id.no_vouchers);
        accumAmountTextView = (TextView) findViewById(R.id.accumulated_amount);

        noVouchers = 0; //TEMP VALUE - GET FROM SERVER
        accumAmount = 0.00F; //TEMP VALUE - GET FROM SERVER

        vouchersTextView.setText(Integer.toString(noVouchers));
        accumAmountTextView.setText(accumAmount + "€");

        expListView = (ExpandableListView) findViewById(R.id.receipts_view);

        listAdapter = new ReceiptAdapter(this, this.receipts, expListView);
        expListView.setAdapter(listAdapter);

        getVouchers();
        requestReceipts();
    }

    private void requestReceipts() {
        try {
            String url = String.format("%s/users/%s/history", getResources().getString(R.string.ip), this.pref.getString("username", null));
            String uuid = this.pref.getString("uuid", null);

            byte[] signed = RSAEncryption.sign(uuid.getBytes(), RSAEncryption.getPrivateKey());
            String signedStr = Base64.getEncoder().encodeToString(signed);

            JSONObject payload = new JSONObject();
            payload.put("uuid", uuid);
            payload.put("uuid_signed", signedStr);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, payload, response -> {
                try {
                    JSONArray receipts = response.getJSONArray("receipts");
                    ArrayList<Receipt> receiptList = this.prepareListData(receipts);

                    ReceiptAdapter adapter = new ReceiptAdapter(this, receiptList, this.expListView);
                    this.expListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ReceiptsActivity.this, "Done!", Toast.LENGTH_SHORT).show();
            }, error -> {
                Toast.makeText(ReceiptsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            });

            this.mQueue.add(req);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //String url = String.format("%s/users/%s", getResources());
    }

    private void getVouchers() {
        String url = String.format("%s/users/%s", getResources().getString(R.string.ip), this.pref.getString("username", null));
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                // Delete saved voucher (it should not persist between accounts).
                SharedPreferences.Editor editor = this.pref.edit();
                editor.remove("voucher");

                // Update no. of available vouchers.
                JSONArray vouchers = response.getJSONArray("vouchers");
                ((TextView) findViewById(R.id.no_vouchers)).setText(String.valueOf(vouchers.length()));

                // Save next voucher on Shared Preferences so it may be consumed on checkout.
                if (vouchers.length() > 0)
                    editor.putString("voucher", response.getJSONArray("vouchers").getString(0));

                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(ReceiptsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        });

        this.mQueue.add(req);
    }

    private ArrayList<Receipt> prepareListData(JSONArray receipts) throws JSONException {
        ArrayList<Receipt> tempReceipts = new ArrayList<Receipt>();

        for (int i = 0; i < receipts.length(); i++) {
            JSONObject receipt = receipts.getJSONObject(i);
            ArrayList<Product> pList = new ArrayList<Product>();

            for (int j = 0; j < receipt.getJSONArray("products").length(); j++) {
                JSONObject pObj = receipt.getJSONArray("products").getJSONObject(j);

                Product product = new Product(pObj.getString("uuid"), (float) pObj.getDouble("price"), pObj.getString("name"));
                pList.add(product);
            }

            Receipt r = new Receipt(new Date(), pList, (float) receipt.getDouble("total"), receipt.has("voucher"));
            tempReceipts.add(r);
        }

        return tempReceipts;
        /*
        // Adding child data
        Product p1 = new Product("id1", 10.5F, "poopoo");
        Product p2 = new Product("id1", 10.5F, "peepee");
        ArrayList<Product> products = new ArrayList<Product>();
        products.add(p1);
        products.add(p2);

        Date date = new Date();

        Receipt r1 = new Receipt(date, products, 10.99F, false);
        Receipt r2 = new Receipt(date, products, 10.99F, false);
        Receipt r3 = new Receipt(date, products, 10.99F, false);
        Receipt r4 = new Receipt(date, products, 10.99F, false);
        this.receipts.add(r1);
        this.receipts.add(r2);
        this.receipts.add(r3);
        this.receipts.add(r4);
        */
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
