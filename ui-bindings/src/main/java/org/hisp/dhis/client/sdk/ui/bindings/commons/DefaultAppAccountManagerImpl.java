package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.hisp.dhis.client.sdk.utils.StringUtils;

/**
 * A singleton class to abstract/wrap and simplify interactions with Account in relation to synchronizing.
 */

public class DefaultAppAccountManagerImpl implements DefaultAppAccountManager {
    private final String TAG = DefaultAppAccountManagerImpl.class.getSimpleName();

    private final Logger logger;
    private final Context appContext;
    private final AppPreferences appPreferences;
    private final UserInteractor currentUserInteractor;
    private final String authority;
    private final String accountType;

    public DefaultAppAccountManagerImpl(Context context,
                                        AppPreferences appPreferences,
                                        UserInteractor currentUserInteractor,
                                        String authority,
                                        String accountType,
                                        Logger logger) {
        this.appContext = context;
        this.appPreferences = appPreferences;
        this.currentUserInteractor = currentUserInteractor;
        this.authority = authority;
        this.accountType = accountType;
        this.logger = logger;
        init();
    }

    private void init() {
        if (!appPreferences.getBackgroundSyncState() || !userIsSignedIn()) {
            logger.i(TAG, "No syncing performed: Synchronizing is turned off or user is not signed in. ");
            return;
        }

        initPeriodicSync();

    }

    private boolean userIsSignedIn() {
        return currentUserInteractor != null && currentUserInteractor.isLoggedIn();
    }

    private Account fetchOrCreateAccount() {
        String accountName = getUsername();

        Account fetchedAccount = fetchAccount(accountName);
        if (fetchedAccount == null) {
            fetchedAccount = createAccount(accountName);
        }

        return fetchedAccount;
    }

    private String getUsername() {
        return currentUserInteractor.username();
    }

    private Account fetchAccount(String accountName) {

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            throw new RuntimeException("Permissions not granted");
        }
        Account accounts[] = ((AccountManager) appContext
                .getSystemService(Context.ACCOUNT_SERVICE))
                .getAccountsByType(accountType);

        for (Account existingAccount : accounts) {
            if (existingAccount.name.equals(accountName)) {
                return existingAccount;
            }
        }

        // no account with this name exists
        return null;
    }

    private Account createAccount(String accountName) {
        if (StringUtils.isEmpty(accountName) || StringUtils.isEmpty(accountType)) {
            Log.i(TAG, "Unable to create account. Account name or account type is invalid");
            return null;
        }

        Account account = new Account(accountName, accountType);
        AccountManager accountManager =
                (AccountManager) appContext.getSystemService(Context.ACCOUNT_SERVICE);

        Boolean accountAddedSuccessfully = accountManager.addAccountExplicitly(account, null, null);
        if (accountAddedSuccessfully) {
            return account;
        } else {
            return null;
        }
    }

    private void initPeriodicSync() {
        if (errorWithAccount()) {
            Log.i(TAG, "Unable to init periodic sync. Account, Authority or Account Type is invalid");
            return;
        }

        if (appPreferences.getBackgroundSyncState()) {
            Account account = fetchOrCreateAccount();
            ContentResolver.setIsSyncable(account, authority, 1);
            ContentResolver.setSyncAutomatically(account, authority, true);
            long minutes = (long) appPreferences.getBackgroundSyncFrequency();
            long seconds = minutes * 60;
            ContentResolver.addPeriodicSync(
                    account,
                    authority,
                    Bundle.EMPTY,
                    seconds);
        }
    }

    private boolean errorWithAccount() {
        return StringUtils.isEmpty(authority) || StringUtils.isEmpty(accountType) ||
                (!accountExists() && createAccount(getUsername()) == null);
    }

    private boolean accountExists() {
        return currentUserInteractor != null && fetchAccount(getUsername()) != null;
    }

    public void setPeriodicSync(int minutes) {
        if (errorWithAccount()) {
            Log.i(TAG, "Unable to set periodic sync.  Account, Authority or Account Type is invalid");
            return;
        }

        Account account = fetchOrCreateAccount();

        ContentResolver.setIsSyncable(account, authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);

        Long seconds = ((long) minutes) * 60;
        ContentResolver.addPeriodicSync(
                account,
                authority,
                Bundle.EMPTY,
                seconds);
    }

    public void syncNow() {
        if (errorWithAccount()) {
            Log.i(TAG, "Unable to set periodic sync.  Account, Authority or Account Type is invalid");
            return;
        }

        Account account = fetchOrCreateAccount();

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(account, authority, settingsBundle);
    }


    public void removeAccount() {
        if (userIsSignedIn() && accountExists()) {
            Account account = fetchAccount(getUsername());
            AccountManager accountManager =
                    (AccountManager) appContext.getSystemService(Context.ACCOUNT_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {

                        try {
                            if (!future.getResult()) {
                                throw new Exception("Unable to remove SyncAdapter stub account. " +
                                        "User must delete the account in Android system settings.");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Unable to remove SyncAdapter stub account", e);
                        }
                    }
                    // TODO remove magic callback implementation - OK
                }, null);

            }

        }
    }

    public void removePeriodicSync() {
        if (errorWithAccount()) {
            Log.i(TAG, "Unable to remove periodic sync.  Account, Authority or Account Type is invalid");
            return;
        }

        Account account = fetchAccount(getUsername());
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
    }
}