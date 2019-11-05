package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ Manifest.permission.CAMERA }, 5);
            }
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.frame_scan_content);
        this.mScannerView = new ZXingScannerView(this);
        contentFrame.addView(this.mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents = " + rawResult.getText() +", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_LONG).show();
        finish();
    }
}
