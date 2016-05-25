package org.hisp.dhis.client.sdk.ui;

import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.ui.AppPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class SyncDateWrapper {
    private static final long DAYS_OLD = 1L;
    private static final long NEVER = 0L;

    // TODO: move strings to resources
    private static final String DATE_FORMAT = "dd/mm/yy hh:mm";
    private static final String NEVER_SYNCED = "never";
    private static final String MIN_AGO = "m ago";
    private static final String HOURS = "h";

    private final AppPreferences appPreferences;
    private final Calendar calendar;

    public SyncDateWrapper(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        this.calendar = Calendar.getInstance();
    }

    public void setLastSyncedNow() {
        long lastSynced = calendar.getTime().getTime();
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

        Long diff = calendar.getTime().getTime() - lastSync;
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
