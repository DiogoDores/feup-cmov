package org.feup.apm.barcodezx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {
  EditText barCodeVal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Button bt;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    barCodeVal = (EditText) findViewById(R.id.msg);
    bt = (Button) findViewById(R.id.bt_generate);
    bt.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    Intent result = new Intent(this, ResultActivity.class);
    result.putExtra("barcodeValue", barCodeVal.getText().toString());
    startActivity(result);
  }
}
