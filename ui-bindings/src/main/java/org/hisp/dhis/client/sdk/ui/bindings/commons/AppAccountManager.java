package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.ui.AppPreferences;

/**
 * A singleton class to abstract/wrap and simplify interactions with Account in relation to synchronizing.
 */
public class AppAccountManager {
    private static final String AUTHORITY = "org.hisp.dhis.android.eventcapture.model.provider";
    private static final String ACCOUNT_TYPE = "org.hisp.dhis.android.eventcapture";
    private static String accountName = "default dhis2 account";

    private final Context appContext;
    private final AppPreferences appPreferences;
    private Account account;

    public AppAccountManager(Context context, AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        this.appContext = context;
        accountName = D2.me().userCredentials().toBlocking().first().getUsername();

        account = createAccount();
        initSyncAccount();
    }

    public void removeAccount() {
        if (account != null && appContext != null) {
            AccountManager accountManager = (AccountManager) appContext
                    .getSystemService(Context.ACCOUNT_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {

                        try {
                            if (!future.getResult()) {
                                throw new Exception("Unable to remove SyncAdapter Stub account. User must delete the account in Android system settings.");
                            }
                        } catch (Exception e) {
                            Log.e("SYNC ADAPTER", "Unable to remove SyncAdapter Stub account", e);
                        }
                    }
                }, new AsyncQueryHandler(new ContentResolver(appContext) {
                }) {
                });
            }

        }
    }

    public Account createAccount() {
        // Create the account type and default account
        Account newAccount = new Account(accountName, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) appContext.getSystemService(Context.ACCOUNT_SERVICE);

        Boolean doesntExist = accountManager.addAccountExplicitly(newAccount, null, null);
        if (doesntExist) {
            account = newAccount;
            return newAccount;
        } else {
            /* The account exists or some other error occurred. Find the account: */
            Account all[] = accountManager.getAccountsByType(ACCOUNT_TYPE);
            for (Account found : all) {
                if (found.equals(newAccount)) {
                    account = newAccount;
                    return found;
                }
            }
        }
        return null; //Error
    }

    public void initSyncAccount() {
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

        if (appPreferences.getBackgroundSyncState()) {
            long minutes = (long) appPreferences.getBackgroundSyncFrequency();
            long seconds = minutes * 60;
            ContentResolver.addPeriodicSync(
                    account,
                    AUTHORITY,
                    Bundle.EMPTY,
                    seconds);
        }
    }

    public void removePeriodicSync() {
        ContentResolver.removePeriodicSync(account, AUTHORITY, Bundle.EMPTY);
    }

    public void setPeriodicSync(int minutes) {
        Long seconds = ((long) minutes) * 60;
        ContentResolver.addPeriodicSync(
                account,
                AUTHORITY,
                Bundle.EMPTY,
                seconds);
    }

    public void syncNow() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        /*
         * Request the syncMetaData for the default account, authority, and
         * manual syncMetaData settings
         */
        ContentResolver.requestSync(account, AUTHORITY, settingsBundle);
    }
}
