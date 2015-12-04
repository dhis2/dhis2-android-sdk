package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.common.network.UserCredentials;
import org.hisp.dhis.java.sdk.common.preferences.IUserPreferences;
import org.hisp.dhis.java.sdk.models.user.UserAccount;
import org.hisp.dhis.java.sdk.user.IUserAccountController;

import rx.Observable;
import rx.Subscriber;

public class UserAccountScope implements IUserAccountScope {
    private final IUserAccountController mUserAccountController;
    private final IUserPreferences mUserPreferences;

    public UserAccountScope(IUserAccountController userAccountController, IUserPreferences userPreferences) {
        mUserAccountController = userAccountController;
        mUserPreferences = userPreferences;
    }

    @Override
    public Observable<UserAccount> signIn(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<UserAccount>() {
            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    if (mUserPreferences.isUserConfirmed()) {
                        throw new IllegalArgumentException("User is already signed in");
                    }

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
