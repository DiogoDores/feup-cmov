package org.feup.apm.selectandnfc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowKeysActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pub_key);
    byte[] modulus = getIntent().getByteArrayExtra("modulus");
    byte[] exponent = getIntent().getByteArrayExtra("exponent");
    byte[] privExp = getIntent().getByteArrayExtra("priv");

    TextView tv = findViewById(R.id.pubkey);
    String text = "Modulus(" + modulus.length + "):\n" + byteArrayToHex(modulus) + "\n" +
                  "Exponent: " + byteArrayToHex(exponent) + "\n" +
                  "Priv Exp(" + privExp.length + "):\n" + byteArrayToHex(privExp);
    tv.setText(text);
  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for (byte b : ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
