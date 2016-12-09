package org.hisp.dhis.android.core.database;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

public class ContentObserverHelper extends ContentObserver {
    private int notificationsReceived;

    public ContentObserverHelper() {
        // listening on main thread
        super(new Handler(Looper.getMainLooper()));

        // no notifications
        notificationsReceived = 0;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        notificationsReceived += 1;
    }

    public int notifications() {
        return notificationsReceived;
//        assertThat(notificationsReceived)
//                .named("Expected %d, but received %d", count, notificationsReceived)
//                .isEqualTo(count);
    }
}
