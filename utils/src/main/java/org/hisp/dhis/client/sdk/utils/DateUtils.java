package org.hisp.dhis.client.sdk.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", LocaleUtils.getLocale());

    private DateUtils() {
        // no instances
    }

    public static DateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    public static Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Map<TimeUnit, Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);

        Map<TimeUnit, Long> result = new LinkedHashMap<>();
        long milliesRest = diffInMillies;
        for (TimeUnit unit : units) {
            long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit, diff);
        }
        return result;
    }

    public static Date plusDays(Date date, int plusDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, plusDays);
        return date;
    }
}
