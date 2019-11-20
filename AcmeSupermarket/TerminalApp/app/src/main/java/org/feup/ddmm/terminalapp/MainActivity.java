package org.feup.ddmm.terminalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mQueue = Volley.newRequestQueue(this);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available in this device", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveReceipt(String username, byte[] data) throws JSONException {
        JSONObject payload = new JSONObject(new String(data));

        // First cast product strings back to JSON format.
        JSONArray products = payload.getJSONArray("products");
        JSONArray fixedProducts = new JSONArray();

        for (int i = 0; i < products.length(); i++) {
            fixedProducts.put(new JSONObject(products.getString(i)));
        }

        payload.remove("products");
        payload.put("products", fixedProducts);

        String url = String.format("%s/users/buy/%s", getString(R.string.ip), username);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, payload, res -> {
            Toast.makeText(this, "Stored receipt!", Toast.LENGTH_SHORT).show();

            try {
                // TODO: Update some text view with this text.
                String total = res.getString("total");
                Toast.makeText(this, total, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) { e.printStackTrace(); }

        }, err -> { Toast.makeText(MainActivity.this, err.toString(), Toast.LENGTH_SHORT).show(); });
        this.mQueue.add(req);
    }

    private void requestPublicKey(String username, byte[] data, byte[] signed) {
        String url = String.format("%s/users/%s", getString(R.string.ip), username);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, res -> {
            try {
                String key = res.getString("public_key");
                PublicKey publicKey = RSAEncryption.getPEMPublicKey(key);

                // After getting the public key, attempt to validate the data with it.
                //if (RSAEncryption.verify(data, signed, publicKey)) {

                    // If the validation was successful, initiate a new post request to save receipt.
                    saveReceipt(username, data);

                //} else {
                //    Toast.makeText(this, "Couldn't validate signature!", Toast.LENGTH_SHORT).show();
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, err -> { Toast.makeText(MainActivity.this, err.toString(), Toast.LENGTH_SHORT).show(); });

        this.mQueue.add(req);
    }

    private void processIntent(Intent intent) {
        //TextView textView = (TextView) findViewById(R.id.text_ndef_main);
        Button button =(Button) findViewById(R.id.checkout_basket_icon);
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_big, 0, 0, 0);
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMessages[0];

        String res = new String(msg.getRecords()[0].getPayload());

        try {
            JSONObject obj = new JSONObject(res);

            String username = obj.getString("username");
            byte[] data = obj.getString("unsigned").getBytes();
            byte[] signed = obj.getString("signed").getBytes();

            // Firstly, request the public key to attempt to validate signature.
            this.requestPublicKey(username, data, signed);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //textView.setText(new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
