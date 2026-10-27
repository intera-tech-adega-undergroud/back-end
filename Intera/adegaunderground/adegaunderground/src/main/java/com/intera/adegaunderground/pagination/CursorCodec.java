package com.intera.adegaunderground.pagination;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

public final class CursorCodec {

    private CursorCodec() {
    }

    public static String scope(String query) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(query.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String encode(String scope, int id) {
        String value = "v1:" + scope + ":" + id;

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static int decode(String cursor, String scope) {
        if (cursor == null) {
            return 0;
        }

        try {
            if (cursor.isBlank() || cursor.length() > 256) {
                throw new IllegalArgumentException();
            }

            String[] parts = new String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8
            ).split(":", -1);

            if (parts.length != 3
                    || !parts[0].equals("v1")
                    || !parts[1].equals(scope)) {
                throw new IllegalArgumentException();
            }

            int id = Integer.parseInt(parts[2]);

            if (id <= 0) {
                throw new IllegalArgumentException();
            }

            return id;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Cursor inválido para esta consulta."
            );
        }
    }
}