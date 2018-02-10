package org.hisp.dhis.android.core.data.http;

import static okhttp3.internal.Util.UTC;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Rfc1123DateFormat extends SimpleDateFormat {
    public Rfc1123DateFormat() {
        super("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        super.setLenient(false);
        super.setTimeZone(UTC);
    }
}
