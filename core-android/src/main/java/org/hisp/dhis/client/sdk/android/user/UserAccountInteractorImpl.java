package org.hisp.dhis.client.sdk.android.user;

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.user.UserAccountController;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;

public class UserAccountInteractorImpl implements UserAccountInteractor {
    // services
    private final UserAccountService userAccountService;

    // controllers
    private final UserAccountController userAccountController;


    public UserAccountInteractorImpl(UserAccountService userAccountService,
                                     UserAccountController userAccountController) {
        this.userAccountService = userAccountService;
        this.userAccountController = userAccountController;
    }

    @Override
    public Observable<UserAccount> sync() {
        return Observable.create(new DefaultOnSubscribe<UserAccount>() {
            @Override
            public UserAccount call() {
                userAccountController.sync();
                return userAccountService.get();
            }
        });
    }

    @Override
    public Observable<UserAccount> pull() {
        return Observable.create(new DefaultOnSubscribe<UserAccount>() {
            @Override
            public UserAccount call() {
                userAccountController.pull();
                return userAccountService.get();
            }
        });
    }

    @Override
    public Observable<UserAccount> push() {
        return Observable.create(new DefaultOnSubscribe<UserAccount>() {
            @Override
            public UserAccount call() {
                userAccountController.push();
                return userAccountService.get();
            }
        });
    }

    @Override
    public Observable<UserAccount> get() {
        return Observable.create(new DefaultOnSubscribe<UserAccount>() {
            @Override
            public UserAccount call() {
                return userAccountService.get();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final UserAccount userAccount) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return userAccountService.save(userAccount);
            }
        });
    }
}
