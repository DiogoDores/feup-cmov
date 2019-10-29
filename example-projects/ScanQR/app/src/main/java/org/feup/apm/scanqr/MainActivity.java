package org.feup.apm.scanqr;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
  static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
  TextView message;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Button QRButton, barButton;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    message = findViewById(R.id.message);
    QRButton = findViewById(R.id.scan1);
    barButton = findViewById(R.id.scan2);
    QRButton.setOnClickListener((v)->scan(true));
    barButton.setOnClickListener((v)->scan(false));
  }

  @Override
  public void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putCharSequence("Message", message.getText());
  }

  public void onRestoreInstanceState(Bundle bundle) {
    super.onRestoreInstanceState(bundle);
    message.setText(bundle.getCharSequence("Message"));
  }

  public void scan(boolean qrcode) {
    try {
      Intent intent = new Intent(ACTION_SCAN);
      intent.putExtra("SCAN_MODE", qrcode ? "QR_CODE_MODE" : "PRODUCT_MODE");
      startActivityForResult(intent, 0);
    }
    catch (ActivityNotFoundException anfe) {
      showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
    }
  }

  private static AlertDialog showDialog(final AppCompatActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
    downloadDialog.setTitle(title);
    downloadDialog.setMessage(message);
    downloadDialog.setPositiveButton(buttonYes, (dialogInterface, i) -> {
      Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      act.startActivity(intent);
    });
    downloadDialog.setNegativeButton(buttonNo, null);
    return downloadDialog.show();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    byte[] baMess;
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        String contents = data.getStringExtra("SCAN_RESULT");
        String format = data.getStringExtra("SCAN_RESULT_FORMAT");
        try {
          baMess = contents.getBytes(StandardCharsets.ISO_8859_1);
        }
        catch (Exception ex) {
          message.setText(ex.getMessage());
          return;
        }
        message.setText("Format: " + format + "\nMessage: " + contents + "\n\nHex: " + byteArrayToHex(baMess));
      }
    }
  }

  String byteArrayToHex(byte[] ba) {
    StringBuilder sb = new StringBuilder(ba.length * 2);
    for(byte b: ba)
      sb.append(String.format("%02x", b));
    return sb.toString();
  }
}
