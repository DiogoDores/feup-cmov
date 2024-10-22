package org.feup.ddmm.acmesupermarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private NfcAdapter nfcAdapter;
    private boolean hasAppliedVoucher = false, hasAppliedDiscount = false;
    private String basket;

    private RequestQueue mQueue;
    private float discount = 0.0f, expense = 0.0f, subtotal = 0.0f, total = 0.0f;

    private void requestInfoUpdate() {
        String url = String.format("%s/users/%s", getString(R.string.ip), this.pref.getString("username", null));

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, res -> {
            try {
                this.expense = (float) res.getDouble("expense");
                this.discount = (float) res.getDouble("discount");

            } catch (JSONException e) { e.printStackTrace(); };

        }, error -> { error.printStackTrace(); });

        this.mQueue.add(req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        this.pref = getSharedPreferences("pref", MODE_PRIVATE);
        this.mQueue = Volley.newRequestQueue(this);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.basket = getIntent().getStringExtra("basket");

        this.subtotal = getIntent().getFloatExtra("total", 0);
        this.total = getIntent().getFloatExtra("total", 0);

        requestInfoUpdate();

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available in this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Default total, subtotal and accumulate values at the start.
        String formattedPrice = Product.formatPrice(this.subtotal);
        ((TextView) findViewById(R.id.subtotal_price_value)).setText(formattedPrice);
        ((TextView) findViewById(R.id.total_price_value)).setText(formattedPrice);
        ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(this.subtotal * 0.15f));

        //Buttons
        Button confirmCheckout = (Button) findViewById(R.id.confirm_checkout_button);
        Button voucherButton = (Button) findViewById(R.id.voucher_checkout_button);
        Button discountButton = (Button) findViewById(R.id.discount_checkout_button);

        //TextViews
        TextView subtotal = (TextView) findViewById(R.id.subtotal_price_value);
        TextView total = (TextView) findViewById(R.id.total_price_value);
        TextView toAccumulate = (TextView) findViewById(R.id.to_accumulate);

        //Confirmation Layouts
        RelativeLayout subtotalPriceLayout = (RelativeLayout) findViewById(R.id.subtotal_price);
        RelativeLayout checkoutDiscountsButtonsLayout = (RelativeLayout) findViewById(R.id.checkout_discounts_buttons);
        RelativeLayout totalPriceCheckoutLayout = (RelativeLayout) findViewById(R.id.total_price_checkout);
        RelativeLayout accumulatedAmountCheckoutLayout = (RelativeLayout) findViewById(R.id.accumulated_amount_checkout);

        //Hidden Views on page creation
        Button basketIcon = (Button) findViewById(R.id.checkout_basket_icon);
        TextView waiting = (TextView) findViewById(R.id.waiting_text_view);

        basketIcon.setVisibility(View.GONE);
        waiting.setVisibility(View.GONE);

        confirmCheckout.setOnClickListener(v -> {
            basketIcon.setVisibility(View.VISIBLE);
            waiting.setVisibility(View.VISIBLE);

            subtotalPriceLayout.setVisibility(View.GONE);
            checkoutDiscountsButtonsLayout.setVisibility(View.GONE);
            totalPriceCheckoutLayout.setVisibility(View.GONE);
            accumulatedAmountCheckoutLayout.setVisibility(View.GONE);
            confirmCheckout.setVisibility(View.GONE);
            confirmCheckout.setEnabled(false);

            this.setupNFC();
        });

        discountButton.setOnClickListener(v -> {
            this.hasAppliedDiscount = !this.hasAppliedDiscount;
            String msg = this.hasAppliedDiscount ? "Discount applied!" : "Discount removed!";

            if (this.hasAppliedDiscount) {
                this.total -= this.expense;
            } else {
                this.total += this.expense;
            }

            if (this.total < 0) {
                ((TextView) findViewById(R.id.total_price_value)).setText(Product.formatPrice(0f));
                ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(0f));
            } else {
                ((TextView) findViewById(R.id.total_price_value)).setText(Product.formatPrice(this.total));
                ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(this.total * 0.15f));
            }

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        });

        // Apply voucher button listener.
        voucherButton.setOnClickListener(v -> {
            String voucher = this.pref.getString("voucher", null), msg = "";
            Resources res = getResources();

            if (this.pref.getString("voucher", null) != null) {
                if (!this.hasAppliedVoucher) {
                    msg = String.format("%s %s!", res.getString(R.string.voucher_applied), voucher);
                    this.total *= 0.85f;
                } else {
                    msg = String.format("%s %s!", res.getString(R.string.voucher_removed), voucher);
                    this.total /= 0.85f;
                }
                ((TextView) findViewById(R.id.total_price_value)).setText(Product.formatPrice(this.total));
                ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(this.total * 0.15f));
            } else {
                msg = String.format("%s", res.getString(R.string.voucher_not_found), voucher);
            }

            if (this.total < 0) {
                ((TextView) findViewById(R.id.total_price_value)).setText(Product.formatPrice(0f));
                ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(0f));
            } else {
                ((TextView) findViewById(R.id.total_price_value)).setText(Product.formatPrice(this.total));
                ((TextView) findViewById(R.id.to_accumulate)).setText(Product.formatPrice(this.total * 0.15f));
            }

            this.hasAppliedVoucher = !this.hasAppliedVoucher;

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    private void setupNFC() {
        String ndefMsg = encryptNdefBasket(basket);

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
    }

    private String encryptNdefBasket(String basket) {
        try {
            JSONObject obj = new JSONObject(this.basket); // Add unsigned basket information.

            if (this.hasAppliedVoucher) {
                obj.put("voucher", this.pref.getString("voucher", null)); // Add voucher UUID.
            }

            obj.put("username", this.pref.getString("username", null)); // Add username.
            obj.put("uuid", this.pref.getString("uuid", null)); // Add user UUID.
            obj.put("apply_discount", this.hasAppliedDiscount); // Add apply discount notice.

            // Sign all the information above.
            byte[] signed = RSAEncryption.sign(obj.toString().getBytes(), RSAEncryption.getPrivateKey());
            obj.put("unsigned", obj.toString());
            obj.put("signed", Base64.getEncoder().encodeToString(signed));

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

