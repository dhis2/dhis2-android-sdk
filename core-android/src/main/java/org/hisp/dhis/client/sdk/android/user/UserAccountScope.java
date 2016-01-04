package org.hisp.dhis.client.sdk.android.user;


import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.IConfigurationPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.user.IUserAccountController;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;
import rx.Subscriber;

public class UserAccountScope implements IUserAccountScope {
    private final IUserAccountController mUserAccountController;
    private final IUserPreferences mUserPreferences;
    private final IConfigurationPreferences mConfigurationPreferences;

    public UserAccountScope(IUserAccountController userAccountController,
                            IUserPreferences userPreferences,
                            IConfigurationPreferences configurationPreferences) {
        mUserAccountController = userAccountController;
        mUserPreferences = userPreferences;
        mConfigurationPreferences = configurationPreferences;
    }

    @Override
    public Observable<UserAccount> signIn(final Configuration configuration, final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<UserAccount>() {

            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    if (mUserPreferences.isUserConfirmed()) {
                        throw new IllegalArgumentException("User is already signed in");
                    }

                    mConfigurationPreferences.save(configuration);

                    UserCredentials userCredentials = new UserCredentials(username, password);
                    mUserPreferences.save(userCredentials);

                    UserAccount userAccount = mUserAccountController.updateAccount();
                    subscriber.onNext(userAccount);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> isSignedIn() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    if(mUserPreferences.isUserConfirmed()) {
                        subscriber.onNext(true);
                    }
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> signOut() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(mUserPreferences.clear());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
