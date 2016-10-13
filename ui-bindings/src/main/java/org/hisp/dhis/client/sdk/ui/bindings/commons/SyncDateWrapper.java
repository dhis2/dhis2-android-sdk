package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.R;

import java.util.Date;

public class SyncDateWrapper {
    //Constants:
    private static final long DAYS_OLD = 1L;
    private static final long NEVER = 0L;

    private final String DATE_FORMAT;
    private final String NEVER_SYNCED;
    private final String MIN_AGO;
    private final String HOURS;
    private final String NOW;

    private final AppPreferences appPreferences;

    public SyncDateWrapper(Context context, AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        DATE_FORMAT = context.getString(R.string.date_format);
        NEVER_SYNCED = context.getString(R.string.never);
        MIN_AGO = context.getString(R.string.min_ago);
        HOURS = context.getString(R.string.hours);
        NOW = context.getString(R.string.now);
    }

    public void setLastSyncedNow() {
        // appPreferences.setLastSynced(DateTime.now().getMillis());
    }

    public void clearLastSynced() {
        appPreferences.setLastSynced(NEVER);
    }

    @Nullable
    public Date getLastSyncedDate() {
//        long lastSynced = appPreferences.getLastSynced();
//
//        if (lastSynced > NEVER) {
//            return new DateTime().withMillis(lastSynced);
//        }
        return null;
    }

    public long getLastSyncedLong() {
        return appPreferences.getLastSynced();
    }

    public String getLastSyncedString() {
//        long lastSync = getLastSyncedLong();
//
//        if (lastSync == NEVER) {
//            return NEVER_SYNCED;
//        }
//
//        DateTime now = DateTime.now();
//        DateTime lastSynced = new DateTime().withMillis(lastSync);
//
//        //older than 24h
//        if (now.minusHours(24).compareTo(lastSynced) == 0) {
//            DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT);
//            return format.print(lastSynced);
//        }
//
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
        return null;
    }
}
