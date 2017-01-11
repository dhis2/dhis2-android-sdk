package org.hisp.dhis.android.core.systeminfo;

import android.support.annotation.NonNull;

import java.util.Date;

public interface SystemInfoStore {

    long insert(@NonNull Date serverDate, @NonNull String dateFormat);

    void close();
}
