package org.feup.apm.qrcodezx;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class MainActivity extends Activity {
  ImageView qrCodeIv;
  TextView messageTv, errorTv;
  final static int DIMENSION=500;
  final static String CH_SET="ISO-8859-1";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    EditText szTv;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    qrCodeIv = findViewById(R.id.img_qr_code);
    messageTv = findViewById(R.id.tv_title);
    errorTv = findViewById(R.id.tv_error);
    szTv = findViewById(R.id.ed_size);
    findViewById(R.id.bt_generate).setOnClickListener((v)->generateQRcode(szTv.getText().toString()));
  }

  void generateQRcode(String sz) {
    int size;
    String content = "";

    if (sz.isEmpty())
      size = 256;
    else
      size = Integer.valueOf(sz);
    if (size < 1)
      size = 1;
    else if (size > 1536)
      size = 1536;
    byte[] bContent = new byte[size];
    for (int b=0; b<size; b++) {
      bContent[b] = (byte)(b%256);
    }
    try {
      content = new String(bContent, CH_SET);
      String print = byteArrayToHex(bContent);
      if (size > 400)
        print = "(too big)";

      messageTv.setText(getApplicationContext().getString(R.string.tv_message_template, size, print));
    }
    catch (UnsupportedEncodingException e) {
      errorTv.setText(e.getMessage());
    }

    final String QRcodeContents = content;
    // convert in a separate thread to avoid possible ANR
    Thread t = new Thread(() -> {
      final Bitmap bitmap = encodeAsBitmap(QRcodeContents);
      runOnUiThread(()->qrCodeIv.setImageBitmap(bitmap));
    });
    t.start();
  }

  Bitmap encodeAsBitmap(String str) {
    BitMatrix result;

    Hashtable<EncodeHintType, String> hints = new Hashtable<>();
    hints.put(EncodeHintType.CHARACTER_SET, CH_SET);
    try {
      result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, DIMENSION, DIMENSION, hints);
    }
    catch (Exception exc) {
      runOnUiThread(()->errorTv.setText(exc.getMessage()));
      return null;
    }
    int w = result.getWidth();
    int h = result.getHeight();
    int colorPrimary = getResources().getColor(R.color.colorPrimary, null);
    int colorWhite = getResources().getColor(R.color.white, null);
    int[] pixels = new int[w * h];
    for (int line = 0; line < h; line++) {
      int offset = line * w;
      for (int col = 0; col < w; col++) {
        pixels[offset + col] = result.get(col, line) ? colorPrimary : colorWhite;
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    return bitmap;
  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for(byte b: ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}