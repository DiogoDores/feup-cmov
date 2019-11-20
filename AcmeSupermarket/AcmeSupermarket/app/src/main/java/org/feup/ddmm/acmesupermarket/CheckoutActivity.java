package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private NfcAdapter nfcAdapter;
    private boolean hasAppliedVoucher;

    private String basket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        this.pref = getSharedPreferences("pref", MODE_PRIVATE);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.basket = getIntent().getStringExtra("basket");
        String ndefMsg = encryptNdefBasket(basket);

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available in this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
            @Override
            public NdefMessage createNdefMessage(NfcEvent event) {
                return new NdefMessage(new NdefRecord[] { createMimeRecord("application/com.example.android.beam", ndefMsg.getBytes()) });
            }
        }, this);

        this.nfcAdapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
            @Override
            public void onNdefPushComplete(NfcEvent event) {
                Toast.makeText(CheckoutActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
            }
        }, this);

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

    private String encryptNdefBasket(String basket) {
        try {
            JSONObject obj = new JSONObject(this.basket); // Add unsigned basket information.

            if (this.hasAppliedVoucher) {
                obj.put("voucher", this.pref.getString("voucher", null)); // Add voucher UUID.
            }

            obj.put("username", this.pref.getString("username", null)); // Add username.
            obj.put("uuid", this.pref.getString("uuid", null)); // Add user UUID.
            obj.put("apply_discount", true); // Add apply discount notice.

            // Sign all the information above.
            byte[] signed = RSAEncryption.sign(obj.toString().getBytes(), RSAEncryption.getPrivateKey());
            String signedStr = Base64.getEncoder().encodeToString(signed);

            obj.put("unsigned", obj.toString());
            obj.put("signed", signedStr);

            return obj.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
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

