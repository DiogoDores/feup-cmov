package org.feup.apm.barcodezx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class ResultActivity extends Activity {
  ImageView barCodeIv;
  TextView errorTv;
  String content = null;
  String errorMsg = "";
  public final static int SIZE=500;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    TextView titleTv;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);
    barCodeIv = findViewById(R.id.img_bar_code_image);
    titleTv = findViewById(R.id.title);
    errorTv = findViewById(R.id.error);

    Intent intent = getIntent();
    content = intent.getStringExtra("barcodeValue");

    titleTv.setText("Value: \"" + content + "\"");

    Thread t = new Thread(new Runnable() {      // do the creation in a new thread to avoid ANR Exception
      public void run() {
        final Bitmap bitmap;

        bitmap = encodeAsBitmap(content);
        runOnUiThread(()->barCodeIv.setImageBitmap(bitmap));
      }
    });
    t.start();
  }

  Bitmap encodeAsBitmap(String str) {
    BitMatrix result;
    try {
      result = new MultiFormatWriter().encode(str, BarcodeFormat.UPC_A, 2*SIZE, SIZE, null);
    }
    catch (Exception exc) {
      errorMsg += "\n" + exc.getMessage();
      runOnUiThread(()->errorTv.setText(errorMsg));
      return null;
    }
    int w = result.getWidth();
    int h = result.getHeight();
    int[] pixels = new int[w * h];
    for (int line = 0; line < h; line++) {
      int offset = line * w;
      for (int col = 0; col < w; col++) {
        pixels[offset + col] = result.get(col, line) ? getResources().getColor(R.color.black):getResources().getColor(R.color.white);
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    return bitmap;
  }
}
