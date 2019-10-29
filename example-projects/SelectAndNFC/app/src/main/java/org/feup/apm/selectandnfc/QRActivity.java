package org.feup.apm.selectandnfc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Hashtable;

public class QRActivity extends AppCompatActivity {
  private final String TAG = "QR_Code";
  private final String ISO_SET = "ISO-8859-1";

  ImageView qrCodeImageview;
  String qr_content = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    TextView titleTv;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qr);
    qrCodeImageview = findViewById(R.id.img_qr_code_image);
    titleTv = findViewById(R.id.title);

    byte[] content = getIntent().getByteArrayExtra("data");
    int sign_size = Constants.KEY_SIZE/8;
    int mess_size = content.length - sign_size;

    try {
      qr_content = new String(content, ISO_SET);
      ByteBuffer bb = ByteBuffer.wrap(content);
      byte[] mess = new byte[mess_size];
      byte[] sign = new byte[sign_size];
      bb.get(mess, 0, mess_size);
      bb.get(sign, 0, sign_size);
      boolean verified = validate(mess, sign);
      String text = "Message: \"" + byteArrayToHex(mess) + "\"\nVerified: " + verified + "\nTotal bytes: " + content.length;
      titleTv.setText(text);
    } catch (UnsupportedEncodingException e) {
      Log.d(TAG, e.getMessage());
    }

    Thread t = new Thread(new Runnable() {  // do the creation in a new thread to avoid ANR Exception
      public void run() {
        final Bitmap bitmap;
        try {
          bitmap = encodeAsBitmap(qr_content);
          runOnUiThread(new Runnable() {  // runOnUiThread method used to do UI task in main thread.
            @Override
            public void run() {
              qrCodeImageview.setImageBitmap(bitmap);
            }
          });
        } catch (WriterException e) {
          Log.d(TAG, e.getMessage());
        }
      }
    });
    t.start();
  }

  Bitmap encodeAsBitmap(String str) throws WriterException {
    int DIMENSION = 600;
    BitMatrix result;

    Hashtable<EncodeHintType, String> hints = new Hashtable<>();
    hints.put(EncodeHintType.CHARACTER_SET, ISO_SET);
    try {
      result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
    } catch (IllegalArgumentException iae) {
      // Unsupported format
      return null;
    }
    int w = result.getWidth();
    int h = result.getHeight();
    int[] pixels = new int[w * h];
    for (int y = 0; y < h; y++) {
      int offset = y * w;
      for (int x = 0; x < w; x++) {
        pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorPrimary) : Color.WHITE;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    return bitmap;
  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for (byte b : ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }

  boolean validate(byte[] message, byte[] signature) {
    boolean verified = false;
    try {
      KeyStore ks = KeyStore.getInstance(Constants.ANDROID_KEYSTORE);
      ks.load(null);
      KeyStore.Entry entry = ks.getEntry(Constants.keyname, null);
      PublicKey pub = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
      Signature sg = Signature.getInstance(Constants.SIGN_ALGO);
      sg.initVerify(pub);
      sg.update(message);
      verified = sg.verify(signature);
    }
    catch (Exception ex) {
      Log.d(TAG, ex.getMessage());
    }
    return verified;
  }
}
