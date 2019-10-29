package org.feup.apm.genproducttag;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class NFCSendActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    NfcAdapter nfcAdapter;
    String mimeType;
    byte[] message;

    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_nfcsend);

    // Check for available NFC Adapter
    nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    if (nfcAdapter == null) {
      Toast.makeText(getApplicationContext(), "NFC is not available on this device.", Toast.LENGTH_LONG).show();
      finish();
    }
    Bundle extras = getIntent().getExtras();
    mimeType = extras.getString("mime");
    message = extras.getByteArray("message");
    NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(mimeType, message) });

    nfcAdapter.setNdefPushMessage(msg, this);       // Register a NDEF message to be sent in P2P
    nfcAdapter.setOnNdefPushCompleteCallback((ev)->{       // when the message is sent ...
      runOnUiThread(() -> {
        Toast.makeText(getApplicationContext(), "Message sent.", Toast.LENGTH_LONG).show();
        finish();
      });
    }, this);
  }

  public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
    byte[] mimeBytes = mimeType.getBytes(StandardCharsets.ISO_8859_1);
    return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
  }
}