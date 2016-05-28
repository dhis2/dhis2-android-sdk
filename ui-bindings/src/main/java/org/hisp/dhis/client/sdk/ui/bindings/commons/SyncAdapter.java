package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;

import javax.inject.Inject;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    ContentResolver mContentResolver;

    // @Inject
    // SyncWrapper syncWrapper;

    @Inject
    SyncDateWrapper syncDateWrapper;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

        //inject the syncWrapper:
       //  ((EventCaptureApp) context.getApplicationContext()).getUserComponent().inject(this);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();

        //inject the syncWrapper:
        // ((EventCaptureApp) context.getApplicationContext()).getUserComponent().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // if (syncWrapper != null) {
        //    Log.d(TAG, "onPerformSync: syncing");

//            syncWrapper.syncMetaData()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<List<ProgramStageDataElement>>() {
//                                   @Override
//                                   public void call(List<ProgramStageDataElement> o) {
//                                       syncDateWrapper.setLastSyncedNow();
//                                       Log.i(TAG, "Synchronization successful.");
//                                   }
//                               }, new Action1<Throwable>() {
//                                   @Override
//                                   public void call(Throwable throwable) {
//                                       //??Log.i(TAG, "Problem with synchronization.");
//                                       Log.e(TAG, "syncMetaData: Exception while syncing! ");
//                                       throwable.printStackTrace();
//                                   }
//                               }
//                    );

        // } else {
        //    Log.d(TAG, "onPerformSync: syncWrapper is null !");
        // }
    }
}