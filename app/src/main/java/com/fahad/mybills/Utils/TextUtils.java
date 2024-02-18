package com.fahad.mybills.Utils;

import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TextUtils {
    public static String decompressText(String compressedData) {
        try {
            byte[] compressedBytes = android.util.Base64.decode(compressedData, android.util.Base64.DEFAULT);
            Inflater inflater = new Inflater();
            inflater.setInput(compressedBytes);

            byte[] buffer = new byte[1024];
            StringBuilder output = new StringBuilder();

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                output.append(new String(buffer, 0, count, StandardCharsets.UTF_8));
            }

            inflater.end();

            return output.toString();
        } catch (DataFormatException e) {
            e.printStackTrace();
            return "";
        }
    }
}
