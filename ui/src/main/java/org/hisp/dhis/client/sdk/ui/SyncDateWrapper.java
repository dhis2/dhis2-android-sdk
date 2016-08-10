package org.hisp.dhis.client.sdk.ui;

import android.content.Context;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SyncDateWrapper {
    //Constants:
    private static final long DAYS_OLD = 1L;
    private static final long NEVER = 0L;

    private final String DATE_FORMAT;
    private final String NEVER_SYNCED;
    private final String MIN_AGO;
    private final String HOURS;

    private final AppPreferences appPreferences;

    public SyncDateWrapper(Context context, AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        DATE_FORMAT = context.getString(R.string.date_format);
        NEVER_SYNCED = context.getString(R.string.never);
        MIN_AGO = context.getString(R.string.min_ago);
        HOURS = context.getString(R.string.hours);
    }

    public void setLastSyncedNow() {
        long lastSynced = Calendar.getInstance().getTime().getTime();
        appPreferences.setLastSynced(lastSynced);
    }

    public void clearLastSynced() {
        appPreferences.setLastSynced(NEVER);
    }

    @Nullable
    public Date getLastSyncedDate() {
        long lastSynced = appPreferences.getLastSynced();

        if (lastSynced > NEVER) {
            Date date = new Date();
            date.setTime(lastSynced);
            return date;
        }
        return null;
    }

    public long getLastSyncedLong() {
        return appPreferences.getLastSynced();
    }

    public String getLastSyncedString() {
        long lastSync = getLastSyncedLong();

        if (lastSync == NEVER) {
            return NEVER_SYNCED;
        }

        Long diff = Calendar.getInstance().getTimeInMillis() - lastSync;
        if (diff >= TimeUnit.DAYS.toMillis(DAYS_OLD)) {
            return new SimpleDateFormat(DATE_FORMAT)
                    .format(getLastSyncedDate());
        }

        Long hours = TimeUnit.MILLISECONDS.toHours(diff);
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(
                diff - TimeUnit.HOURS.toMillis(hours));

        String result = "";
        if (hours > 0) {
            result += hours + HOURS;
        }
        result += minutes + MIN_AGO;
        return result;
    }
}
