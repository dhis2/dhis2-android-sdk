package org.hisp.dhis.android.core.data.http;

import java.text.ParseException;
import java.util.Date;

public class HttpHeaderDate {
    private final Date date;
    private static final Rfc1123DateFormat RFC_1123_DATE_FORMAT = new Rfc1123DateFormat();

    public HttpHeaderDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }

        this.date = date;
    }

    public static HttpHeaderDate parse(String source) throws ParseException {
        Date date = RFC_1123_DATE_FORMAT.parse(source);

        return new HttpHeaderDate(date);
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    @Override
    public String toString() {
        String dateHeaderValue = RFC_1123_DATE_FORMAT.format(date);

        return dateHeaderValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpHeaderDate that = (HttpHeaderDate) o;

        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}

