package org.feup.apm.selectandnfc;

import java.nio.charset.Charset;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class NFCSendActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    NfcAdapter nfcAdapter;
    String mimeType;
    byte[] message;

    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_nfcsend_key);

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

    // Register a NDEF message to be sent in P2P
    nfcAdapter.setNdefPushMessage(msg, this);
    nfcAdapter.setOnNdefPushCompleteCallback(this, this);
  }

  public void onNdefPushComplete(NfcEvent arg0) {
    runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(getApplicationContext(), "Message sent.", Toast.LENGTH_LONG).show();
        finish();
      }
    });
  }

  public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
    byte[] mimeBytes = mimeType.getBytes(Charset.forName("ISO-8859-1"));
    return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
  }
}
