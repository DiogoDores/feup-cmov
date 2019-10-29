package org.feup.apm.selectandnfc;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.security.KeyPairGeneratorSpec;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import javax.security.auth.x500.X500Principal;

public class MainActivity extends AppCompatActivity {
  private final String TAG = "SelectAndNFC";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final Context context = this;

    final ListView list = findViewById(R.id.itemlist);
    Button but_send = findViewById(R.id.bSend);
    Button but_qr = findViewById(R.id.bQRcode);
    final ArrayAdapter<Product> adapter = new ModelAdapter(this, getProducts());
    list.setAdapter(adapter);
    but_send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        byte[] message = buildMessage(adapter);
        Intent intent = new Intent(context, NFCSendActivity.class);
        intent.putExtra("message", message);
        intent.putExtra("mime", "application/nfc.feup.apm.ordermsg");
        startActivity(intent);
      }
    });
    but_qr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        byte[] message = buildMessage(adapter);
        Intent intent = new Intent(context, QRActivity.class);
        intent.putExtra("data", message);
        startActivity(intent);
      }
    });
    generateAndStoreKeys();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    PubKey pkey = getPubKey();
    byte[] privExp = getPrivExp();
    switch (item.getItemId()) {
      case R.id.spublic:
        Intent intent = new Intent(this, NFCSendActivity.class);
        intent.putExtra("message", pkey.modulus);
        intent.putExtra("mime", "application/nfc.feup.apm.pubkeymsg");
        startActivity(intent);
        break;
      case R.id.skeys:
        Intent showIntent = new Intent(this, ShowKeysActivity.class);
        showIntent.putExtra("modulus", pkey.modulus);
        showIntent.putExtra("exponent", pkey.exponent);
        showIntent.putExtra("priv", privExp);
        startActivity(showIntent);
        break;
      default:
        return false;
    }
    return true;
  }

  private List<Product> getProducts() {
    List<Product> list = new ArrayList<>();
    for (int k=0; k<Product.products.length; k++)
      list.add(new Product(Product.products[k], k+1));
    return list;
  }

  private byte[] buildMessage(ArrayAdapter<Product> ad) {
    ArrayList<Integer> sels = new ArrayList<>();
    int nitems = ad.getCount();
    for (int k = 0; k < nitems; k++)
      if (ad.getItem(k).selected)
        sels.add(ad.getItem(k).type);
    int nr = sels.size();
    ByteBuffer bb = ByteBuffer.allocate((nr+1)+Constants.KEY_SIZE/8);
    bb.put((byte)nr);
    for (int k=0; k<nr; k++)
      bb.put(sels.get(k).byteValue());
    byte[] message = bb.array();
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      PrivateKey pri = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
      Signature sg = Signature.getInstance(Constants.SIGN_ALGO);
      sg.initSign(pri);
      sg.update(message, 0, nr+1);
      int sz = sg.sign(message, nr+1, Constants.KEY_SIZE/8);
      Log.d(TAG, "Sign size = " + sz + " bytes.");
    }
    catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
    }
    return message;
  }

  private void generateAndStoreKeys(){
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      if (entry == null) {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 20);
        KeyPairGenerator kgen = KeyPairGenerator.getInstance(Constants.KEY_ALGO, Constants.ANDROID_KEYSTORE);
        AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(this)
            .setKeySize(Constants.KEY_SIZE)
            .setAlias(Constants.keyname)
            .setSubject(new X500Principal("CN=" + Constants.keyname))   // Usually the full name of the owner (person or organization)
            .setSerialNumber(BigInteger.valueOf(12121212))
            .setStartDate(start.getTime())
            .setEndDate(end.getTime())
            .build();
        kgen.initialize(spec);
        kgen.generateKeyPair();
      }
    }
    catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
    }
  }

  PubKey getPubKey() {
    PubKey pkey = new PubKey();
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
      pkey.modulus = ((RSAPublicKey)pub).getModulus().toByteArray();
      pkey.exponent = ((RSAPublicKey)pub).getPublicExponent().toByteArray();
    }
    catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
    }
    return pkey;
  }

  byte[] getPrivExp() {
    byte[] exp = null;

    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      PrivateKey priv = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
      exp = ((RSAPrivateKey)priv).getPrivateExponent().toByteArray();
    }
    catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
    }
    if (exp == null)
      exp = new byte[0];
    return exp;
  }

  class PubKey {
    byte[] modulus;
    byte[] exponent;
  }
}