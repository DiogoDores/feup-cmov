package org.feup.apm.terminalnfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/* This Activity is finished when it starts another (manifest with noHistory = true) */
public class NFCReceiveOrderActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
      processIntent(getIntent());
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
  }

  void processIntent(Intent intent) {                                                          // gets an order message and passes it to the MainActivity
    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    NdefMessage msg = (NdefMessage)rawMsgs[0];
    byte[] message = msg.getRecords()[0].getPayload();
    Intent main = new Intent(this, MainActivity.class);
    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    main.putExtra("order", message);
    main.putExtra("type", 2);
    startActivity(main);
  }
}
