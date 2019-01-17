package org.hisp.dhis.android.core.sms.domain.converter;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

final class Utility {
    private Utility() {
    }

    static String check(String value) {
        return check(value, true);
    }

    static String check(String value, boolean checkLength) {
        if (value == null) {
            throw new NullPointerException("Null value given to sms conversion");
        }
        if (checkLength && value.length() == 0) {
            throw new NullPointerException("Empty value given to sms conversion");
        }
        return value;
    }

    static String addCheckSum(String data) throws NoSuchAlgorithmException {
        MessageDigest mdEnc = MessageDigest.getInstance("MD5");
        mdEnc.update(data.getBytes(Util.UTF_8), 0, data.length());
        StringBuilder md5 = new StringBuilder(new BigInteger(1, mdEnc.digest()).toString(16));
        while (md5.length() < 32) {
            md5.insert(0, "0");
        }
        md5.append(data);
        return md5.toString();
    }

    static String timestamp() {
        // TODO discuss what timezone and format should be used. Always UTC?
        return new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.ROOT).format(new Date());
    }
}
