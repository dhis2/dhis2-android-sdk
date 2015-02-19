package org.hisp.dhis2.android.sdk.network.managers;

import com.squareup.okhttp.Credentials;

public final class Base64Manager {

    public static String toBase64(String username, String password) {
        return Credentials.basic(username, password);
    }
}
