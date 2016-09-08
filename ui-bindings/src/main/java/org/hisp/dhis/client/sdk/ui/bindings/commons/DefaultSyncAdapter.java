package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

public interface DefaultSyncAdapter {

    void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult);

    IBinder getSyncAdapterBinder();
}
