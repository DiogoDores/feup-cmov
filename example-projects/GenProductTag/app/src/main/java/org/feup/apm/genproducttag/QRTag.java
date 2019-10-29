package org.feup.apm.genproducttag;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class QRTag extends AppCompatActivity {
  private final String TAG = "QR_Code";

  ImageView qrCodeImageview;
  String qr_content = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrtag);
    qrCodeImageview = findViewById(R.id.img_qr_code);

    byte[] content = getIntent().getByteArrayExtra("data");
    qr_content = new String(content, StandardCharsets.ISO_8859_1);

    Thread t = new Thread(() -> {              // do the creation in a new thread to avoid ANR Exception
      final Bitmap bitmap;
      try {
        bitmap = encodeAsBitmap(qr_content);
        runOnUiThread(() -> {                  // runOnUiThread method used to do UI task in main thread.
          qrCodeImageview.setImageBitmap(bitmap);
        });
      }
      catch (Exception e) {
        Log.d(TAG, e.getMessage());
      }
    });
    t.start();
  }

  Bitmap encodeAsBitmap(String str) throws WriterException {
    int DIMENSION = 1000;
    BitMatrix result;

    Hashtable<EncodeHintType, String> hints = new Hashtable<>();
    hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
    try {
      result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
    }
    catch (IllegalArgumentException iae) {
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
}
