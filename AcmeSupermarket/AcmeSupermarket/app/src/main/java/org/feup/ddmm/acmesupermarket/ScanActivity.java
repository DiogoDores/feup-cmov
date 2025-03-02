package org.feup.ddmm.acmesupermarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;

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
    public void handleResult(Result result) {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String smPublicKey = preferences.getString("sm_public_key", null);

        try {
            PublicKey publicKey = RSAEncryption.getPEMPublicKey(smPublicKey);
            byte[] msgBytes = RSAEncryption.convertHexToByteArray(result.getText());
            byte[] decrypted = RSAEncryption.decrypt(msgBytes, publicKey);
            JSONObject obj = BufferHandler.decipherTag(decrypted);

            Intent intent = new Intent();
            intent.putExtra("MESSAGE", obj.toString());
            setResult(1, intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
