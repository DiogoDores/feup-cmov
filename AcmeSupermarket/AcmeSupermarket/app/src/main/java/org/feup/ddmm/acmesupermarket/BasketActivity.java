package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class BasketActivity extends AppCompatActivity {
    private ArrayList<Product> basket = new ArrayList<Product>();
    private NfcAdapter nfcAdapter;
    ListView listView;
    BasketAdapter adapter;
    TextView totalPriceView;
    private float totalPrice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        findViewById(R.id.button_camera_open).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, ScanActivity.class), 1);
        });

        findViewById(R.id.start_checkout_button).setOnClickListener(v -> openCheckoutActivity());

        totalPriceView = (TextView) findViewById(R.id.total_price);
        totalPrice = 0.00F;

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available in this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
            @Override
            public NdefMessage createNdefMessage(NfcEvent event) {
                String text = "godspeed you! black emperor";
                return new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.example.android.beam", text.getBytes()) });
            }
        }, this);

        this.nfcAdapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
            @Override
            public void onNdefPushComplete(NfcEvent event) {
                Toast.makeText(BasketActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
            }
        }, this);

        listView = (ListView)findViewById(R.id.list_view);

        adapter = new BasketAdapter(this, R.layout.list_adapter_view, this.basket);
        listView.setAdapter(adapter);
    }

    public void openCheckoutActivity() {
        startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Toast.makeText(this, data.getStringExtra("MESSAGE"), Toast.LENGTH_SHORT).show();

            boolean inBasket = false;

            // Convert string to JSON and to Product object and push it to basket.
            Gson gson = new Gson();
            Product product = gson.fromJson(data.getStringExtra("MESSAGE"), Product.class);

            for(int i = 0; i < basket.size(); i++){
                if(basket.get(i).getUuid().equals(product.getUuid())){
                    basket.get(i).incrementQuantity();
                    inBasket = true;
                }
            }
            totalPrice += product.getPrice();
            totalPriceView.setText(Product.formatPrice(totalPrice));

            if(!inBasket)
                this.basket.add(product);
            this.adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
