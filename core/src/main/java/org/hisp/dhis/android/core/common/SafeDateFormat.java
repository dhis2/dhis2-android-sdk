package org.hisp.dhis.android.core.common;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SafeDateFormat {

    @NonNull
    private final ThreadSafeDateFormat dateFormat;

    public SafeDateFormat(@NonNull String pattern) {
        this.dateFormat = new ThreadSafeDateFormat(pattern);
    }

    @NonNull
    public Date parse(@NonNull String pattern) throws ParseException {
        return dateFormat.get().parse(pattern);
    }

    @NonNull
    public String format(@NonNull Date date) {
        return dateFormat.get().format(date);
    }

    @NonNull
    public DateFormat raw() {
        return dateFormat.get();
    }

    private static class ThreadSafeDateFormat extends ThreadLocal<DateFormat> {
        private final String pattern;

        public ThreadSafeDateFormat(String pattern) {
            this.pattern = pattern;
        }

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(pattern, Locale.US);
        }
    }
}