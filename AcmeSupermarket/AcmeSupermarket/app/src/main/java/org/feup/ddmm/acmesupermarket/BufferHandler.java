package org.feup.ddmm.acmesupermarket;

import android.util.Log;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BufferHandler {
    public static String decipherTag(byte[] clearTag) {
        JSONObject product = new JSONObject();

        ByteBuffer tag = ByteBuffer.wrap(clearTag);
        int tagId = tag.getInt();
        UUID id = new UUID(tag.getLong(), tag.getLong());
        int euros = tag.getInt(), cents = tag.getInt();
        byte[] bName = new byte[tag.get()];
        tag.get(bName);

        return new String(bName, StandardCharsets.ISO_8859_1);
    }
}
