package org.feup.ddmm.terminalapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncryption {
    public static PublicKey getPEMPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        String clean = key.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?|\\n)", "");
        byte[] decoded;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decoded = Base64.getDecoder().decode(clean.getBytes());
        } else {
            decoded = android.util.Base64.decode(clean.getBytes(), android.util.Base64.DEFAULT);
        }
        return kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    public static boolean verify(byte[] data, byte[] signed, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signed);
    }
}
