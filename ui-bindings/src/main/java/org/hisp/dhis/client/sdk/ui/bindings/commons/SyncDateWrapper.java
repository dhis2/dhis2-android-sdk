package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.utils.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SyncDateWrapper {
    //Constants:
    //    private static final long DAYS_OLD = 1L;
    private static final long NEVER = 0L;

    //    private final String DATE_FORMAT;
    private final String NEVER_SYNCED = "never";
    private final String MIN_AGO = "m ago";
    private final String HOURS = "h";
    private final String NOW = "now";

    private final AppPreferences appPreferences;

    public SyncDateWrapper(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
//        DATE_FORMAT = context.getString(R.string.date_format);
    }

    public void setLastSyncedNow() {
        appPreferences.setLastSynced(Calendar.getInstance().getTime());
    }

    public void clearLastSynced() {
        appPreferences.setLastSynced(new Date(0));
    }

    @Nullable
    public Date getLastSyncedDate() throws ParseException {
        return appPreferences.getLastSynced();
//        long lastSynced = appPreferences.getLastSynced();
//
//        if (lastSynced > NEVER) {
//            return new DateTime().withMillis(lastSynced);
//        }
//        return null;
    }

    public Date getLastSyncedDateObject() throws ParseException {
        return appPreferences.getLastSynced();
    }

    public String getLastSyncedString() throws ParseException {
        Date lastSync = getLastSyncedDateObject();

        if (lastSync.getTime() == NEVER) {
            return NEVER_SYNCED;
        }

        Date now = Calendar.getInstance().getTime();
        Map<TimeUnit, Long> map = DateUtils.computeDiff(lastSync, now);

        if (map.get(TimeUnit.DAYS) > 0) {
            return DateUtils.getDateFormat().format(lastSync);
        } else if (map.get(TimeUnit.HOURS) > 0) {
            return (map.get(TimeUnit.HOURS) + " " + HOURS);
        } else if (map.get(TimeUnit.MINUTES) > 0) {
            return (map.get(TimeUnit.MINUTES) + " " + MIN_AGO);
        } else {
            return NOW;
        }
        //older than 24h
//        if (now.minusHours(24).compareTo(lastSynced) == 0) {
//            DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT);
//            return format.print(lastSynced);
//        }

//        Period period = new Period(lastSynced, now);
//
//        PeriodFormatter formatter = new PeriodFormatterBuilder()
//                .appendHours().appendSuffix(HOURS)
//                .appendSeparator(" ")
//                .appendMinutes().appendSuffix(MIN_AGO)
//                .toFormatter();
//
//        String result = formatter.print(period);
//        if (result.isEmpty()) {
//            result = NOW;
//        }
//        return result;
//        return null;
    }
}
