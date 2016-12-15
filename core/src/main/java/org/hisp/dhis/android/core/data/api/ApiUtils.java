package org.hisp.dhis.android.core.data.api;

import android.support.annotation.NonNull;

import java.io.UnsupportedEncodingException;

import okio.ByteString;

public final class ApiUtils {
    private ApiUtils() {
        // no instances
    }

    @NonNull
    public static String base64(@NonNull String username, @NonNull String password) {
        try {
            String usernameAndPassword = username + ":" + password;
            byte[] bytes = usernameAndPassword.getBytes("ISO-8859-1");
            return ByteString.of(bytes).base64();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new AssertionError(unsupportedEncodingException);
        }
    }
}
