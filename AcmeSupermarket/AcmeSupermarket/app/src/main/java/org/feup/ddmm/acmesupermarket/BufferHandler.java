package org.feup.ddmm.acmesupermarket;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BufferHandler {
    public static JSONObject decipherTag(byte[] clearTag) throws JSONException {
        JSONObject product = new JSONObject();
        ByteBuffer tag = ByteBuffer.wrap(clearTag);

        product.put("uuid", new UUID(tag.getLong(), tag.getLong()));
        product.put("price", tag.getInt() + tag.getInt() / 100.0);

        byte[] name = new byte[tag.get()];
        tag.get(name);
        product.put("name", new String(name, StandardCharsets.ISO_8859_1));

        return product;
    }
}
