package org.feup.apm.readtags;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {
  static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
  boolean hasKey = false;
  PublicKey pub;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    readKey();
  }

  /* The NFC messages are received in their own activities and sent to the MainActivity */
  @Override
  public void onResume() {
    super.onResume();
    int type = getIntent().getIntExtra("type", 0);
    if (type == 1)
      showAndStoreKey(getIntent().getByteArrayExtra("cert"));                // get the NFC message (public key certificate)
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.mn_scan)
      scanQRCode();
    return true;
  }

  void readKey() {
    TextView tvTitle = findViewById(R.id.tv_title);

    try {
      KeyStore keyStore = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      keyStore.load(null);
      Certificate cert = keyStore.getCertificate(Constants.keyAlias);
      if (cert != null) {
        pub = cert.getPublicKey();
        hasKey = true;
        tvTitle.setText(R.string.msg_keyinfo);
      }
    }
    catch(Exception e) {
      tvTitle.setText(e.getMessage());
    }
    if (hasKey) {
      String text = "Public Key:\nModulus: " + byteArrayToHex(((RSAPublicKey)pub).getModulus().toByteArray()) + "\n" +
                    "Exponent: " + byteArrayToHex(((RSAPublicKey)pub).getPublicExponent().toByteArray());
      ((TextView)findViewById(R.id.tv_text)).setText(text);
    }
  }

  void showAndStoreKey(byte[] cert) {
    TextView tvTitle = findViewById(R.id.tv_title);

    try {
      KeyStore keyStore = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      keyStore.load(null);
      X509Certificate x509 = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(cert));
      keyStore.setEntry(Constants.keyAlias, new KeyStore.TrustedCertificateEntry(x509), null);
      pub = x509.getPublicKey();
      hasKey = true;
      tvTitle.setText(R.string.msg_keyinfo);
    }
    catch(Exception e) {
      tvTitle.setText(e.getMessage());
    }
    String text = "Public Key:\nModulus: " + byteArrayToHex(((RSAPublicKey)pub).getModulus().toByteArray()) + "\n" +
                  "Exponent: " + byteArrayToHex(((RSAPublicKey)pub).getPublicExponent().toByteArray());
    ((TextView)findViewById(R.id.tv_text)).setText(text);
  }

  public void scanQRCode() {
    try {
      Intent intent = new Intent(ACTION_SCAN);
      intent.putExtra("SCAN_MODE",  "QR_CODE_MODE");
      startActivityForResult(intent, 0);
    }
    catch (ActivityNotFoundException anfe) {
      showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
    }
  }

  private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
    downloadDialog.setTitle(title);
    downloadDialog.setMessage(message);
    downloadDialog.setPositiveButton(buttonYes, (d, i) -> {
        Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        act.startActivity(intent);
    });
    downloadDialog.setNegativeButton(buttonNo, null);
    return downloadDialog.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        String contents = data.getStringExtra("SCAN_RESULT");
        if (contents != null)
          decodeAndShow(contents.getBytes(StandardCharsets.ISO_8859_1));
      }
    }
  }

  void decodeAndShow(byte[] encTag) {
    TextView tvTitle = findViewById(R.id.tv_title);
    byte[] clearTag;

    try {
      Cipher cipher = Cipher.getInstance(Constants.ENC_ALGO);
      cipher.init(Cipher.DECRYPT_MODE, pub);
      clearTag = cipher.doFinal(encTag);
    }
    catch (Exception e) {
      tvTitle.setText(e.getMessage());
      return;
    }
    ByteBuffer tag = ByteBuffer.wrap(clearTag);
    int tId = tag.getInt();
    long most = tag.getLong();
    long less = tag.getLong();
    UUID id = new UUID(most, less);
    int euros = tag.getInt();
    int cents = tag.getInt();
    byte l = tag.get();
    byte[] bName = new byte[l];
    tag.get(bName);
    String name = new String(bName, StandardCharsets.ISO_8859_1);

    String text = "Read Tag (" + clearTag.length + "):\n" + byteArrayToHex(clearTag) + "\n\n" +
        ((tId==Constants.tagId)?"correct":"wrong") + "\n" +
        "ID: " + id.toString() + "\n" +
        "Name: " + name + "\n" +
        "Price: €" + euros + "." + cents;
    ((TextView)findViewById(R.id.tv_text)).setText(text);
    tvTitle.setText(R.string.msg_taginfo);
  }

  String byteArrayToHex(byte[] ba) {                              // converter
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for (byte b : ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
