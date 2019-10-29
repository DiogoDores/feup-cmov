package org.feup.apm.genproducttag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class MainActivity extends AppCompatActivity {
  final String TAG = "GenProductTag";
  UUID productUUID;
  boolean hasKey = false;
  PrivateKey pri;
  PublicKey pub;
  byte[] encTag;

  EditText edUUID, edName, edEuros, edCents;
  TextView tvNoKey, tvKey;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    edUUID = findViewById(R.id.ed_uuid);
    edName = findViewById((R.id.ed_name));
    edEuros = findViewById(R.id.ed_euros);
    edCents = findViewById((R.id.ed_cents));
    tvNoKey = findViewById(R.id.tv_nokey);
    tvKey = findViewById(R.id.tv_pubkey);
    findViewById(R.id.bt_ugen).setOnClickListener((v)->{
      productUUID =  UUID.randomUUID();
      edUUID.setText(productUUID.toString());
    });
    findViewById(R.id.bt_gentag).setOnClickListener((v)->genTag());
    findViewById(R.id.bt_dectag).setOnClickListener((v)->decTag());
    findViewById(R.id.bt_genkey).setOnClickListener((v)->genKeys());
    findViewById(R.id.bt_send).setOnClickListener((v)->sendKey());
    findViewById(R.id.bt_showkey).setOnClickListener((v)->showKey());
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      hasKey = (entry != null);
      if (hasKey) {
        pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
        pri = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
        tvNoKey.setText("");
      }
    }
    catch (Exception e) {
      hasKey = false;
    }
    findViewById(R.id.bt_send).setEnabled(hasKey);
    findViewById(R.id.bt_showkey).setEnabled(hasKey);
    findViewById(R.id.bt_genkey).setEnabled(!hasKey);
  }

  void genTag() {
    ByteBuffer tag;
    String name = edName.getText().toString();

    if (!hasKey || edUUID.getText().toString().length() == 0 || name.length() == 0 ||
        edEuros.getText().toString().length() == 0 || edCents.getText().toString().length() == 0) {
      tvNoKey.setText(R.string.msg_empty);
      return;
    }
    int len = 4 + 16 + 4 + 4 + 1 + edName.getText().toString().length();
    int l = edName.getText().toString().length();
    if (l > 35)
      name = edName.getText().toString().substring(0, 35);
    tag = ByteBuffer.allocate(len);
    tag.putInt(Constants.tagId);
    tag.putLong(productUUID.getMostSignificantBits());
    tag.putLong(productUUID.getLeastSignificantBits());
    tag.putInt(Integer.parseInt(edEuros.getText().toString()));
    tag.putInt(Integer.parseInt(edCents.getText().toString()));
    tag.put((byte)name.length());
    tag.put(name.getBytes(StandardCharsets.ISO_8859_1));

    try {
      Cipher cipher = Cipher.getInstance(Constants.ENC_ALGO);
      cipher.init(Cipher.ENCRYPT_MODE, pri);
      encTag = cipher.doFinal(tag.array());
      tvKey.setText(getResources().getString(R.string.msg_enctag, encTag.length, byteArrayToHex(encTag)));
      tvNoKey.setText("");
    }
    catch (Exception e) {
      tvNoKey.setText(e.toString());
    }
    Intent qrAct = new Intent(this, QRTag.class);
    qrAct.putExtra("data", encTag);
    startActivity(qrAct);
  }

  void decTag() {
    byte[] clearTag;

    if (encTag == null || encTag.length == 0) {
      tvNoKey.setText(R.string.msg_notag);
      return;
    }
    try {
      Cipher cipher = Cipher.getInstance(Constants.ENC_ALGO);
      cipher.init(Cipher.DECRYPT_MODE, pub);
      clearTag = cipher.doFinal(encTag);
    }
    catch (Exception e) {
      tvNoKey.setText(e.getMessage());
      return;
    }
    ByteBuffer tag = ByteBuffer.wrap(clearTag);
    int tId = tag.getInt();
    UUID id = new UUID(tag.getLong(), tag.getLong());
    int euros = tag.getInt();
    int cents = tag.getInt();
    byte[] bName = new byte[tag.get()];
    tag.get(bName);
    String name = new String(bName, StandardCharsets.ISO_8859_1);

    String text = "DecTag (" + clearTag.length + "):\n" + byteArrayToHex(clearTag) + "\n\n" +
                  ((tId==Constants.tagId)?"correct":"wrong") + "\n" +
                  "ID: " + id.toString() + "\n" +
                  "Name: " + name + "\n" +
                  "Price: €" + euros + "." + cents;
    tvNoKey.setText("");
    tvKey.setText(text);
  }

  void genKeys() {
    String text;

    try {
      Calendar start = new GregorianCalendar();
      Calendar end = new GregorianCalendar();
      end.add(Calendar.YEAR, 20);
      KeyPairGenerator kgen = KeyPairGenerator.getInstance(Constants.KEY_ALGO, Constants.ANDROID_KEYSTORE);
      AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(this)
          .setKeySize(Constants.KEY_SIZE)
          .setAlias(Constants.keyname)
          .setSubject(new X500Principal("CN=" + Constants.keyname))
          .setSerialNumber(BigInteger.valueOf(Constants.CERT_SERIAL))
          .setStartDate(start.getTime())
          .setEndDate(end.getTime())
          .build();
      kgen.initialize(spec);
      KeyPair kp = kgen.generateKeyPair();
      pri = kp.getPrivate();                                         // private key in a Java class (PrivateKey)
      pub = kp.getPublic();                                          // the corresponding public key in a Java class (PublicKey)
      hasKey = true;
    }
    catch (Exception e) {
      tvNoKey.setText(e.getMessage());
      return;
    }
    findViewById(R.id.bt_send).setEnabled(hasKey);
    findViewById(R.id.bt_showkey).setEnabled(hasKey);
    findViewById(R.id.bt_genkey).setEnabled(!hasKey);
    tvNoKey.setText("");
    byte[] modulus = ((RSAPublicKey)pub).getModulus().toByteArray();
    text = "Modulus (" + modulus.length + "):\n";
    text += byteArrayToHex(modulus) + "\n";
    text += "Public Exponent:\n";
    text += byteArrayToHex(((RSAPublicKey)pub).getPublicExponent().toByteArray()) + "\n";
    tvKey.setText(text);
  }

  void sendKey() {
    X509Certificate cert;

    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      if (entry != null) {
        cert = (X509Certificate)((KeyStore.PrivateKeyEntry)entry).getCertificate();
        tvKey.setText(cert.toString());
        Intent intent = new Intent(this, NFCSendActivity.class);
        intent.putExtra("message", cert.getEncoded());
        intent.putExtra("mime", "application/nfc.feup.apm.pubkeyfortag");
        startActivity(intent);
      }
    }
    catch (Exception e) {
      tvNoKey.setText(e.getMessage());
    }
  }

  void showKey() {
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      if (entry != null) {
        X509Certificate cert = (X509Certificate)((KeyStore.PrivateKeyEntry)entry).getCertificate();
        byte[] encCert = cert.getEncoded();
        String strCert = cert.toString();
        String b64Cert = Base64.encodeToString(encCert, Base64.DEFAULT);
        String text = "cert(b64): " + b64Cert + "\n\n" + strCert;
        tvKey.setText(text);
        text = "-----BEGIN CERTIFICATE-----\n" + b64Cert +
               "-----END CERTIFICATE-----\n";
        Log.d(TAG, text);
      }
    }
    catch (Exception e) {
      tvNoKey.setText(e.getMessage());
    }
  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for(byte b: ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
