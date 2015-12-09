package org.hisp.dhis.android.sdk.user;

import org.hisp.dhis.java.sdk.common.network.UserCredentials;
import org.hisp.dhis.java.sdk.common.preferences.IUserPreferences;
import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.user.UserAccount;
import org.hisp.dhis.java.sdk.user.IUserAccountController;
import org.hisp.dhis.java.sdk.user.IUserAccountService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class UserAccountScope implements IUserAccountScope {
    private final IUserAccountController mUserAccountController;
    private final IUserAccountService mUserAccountService;
    private final IUserPreferences mUserPreferences;

    public UserAccountScope(IUserAccountController userAccountController, IUserAccountService mUserAccountService, IUserPreferences userPreferences) {
        mUserAccountController = userAccountController;
        this.mUserAccountService = mUserAccountService;
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

    @Override
    public Observable<List<Program>> listAssignedPrograms(final OrganisationUnit organisationUnit) {
        return Observable.create(new Observable.OnSubscribe<List<Program>>() {

            @Override
            public void call(Subscriber<? super List<Program>> subscriber) {
                try {
                    List<Program> programs = mUserAccountService.listAssignedPrograms(organisationUnit);
                    subscriber.onNext(programs);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<OrganisationUnit>> listAssignedOrganisationUnits() {
        return Observable.create(new Observable.OnSubscribe<List<OrganisationUnit>>() {

            @Override
            public void call(Subscriber<? super List<OrganisationUnit>> subscriber) {
                try {
                    List<OrganisationUnit> organisationUnits = mUserAccountService.listAssignedOrganisationUnits();
                    subscriber.onNext(organisationUnits);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<OrganisationUnit>> synchronizeAssignedPrograms() {
        return Observable.create(new Observable.OnSubscribe<List<OrganisationUnit>>() {

            @Override
            public void call(Subscriber<? super List<OrganisationUnit>> subscriber) {
                try {
                    List<OrganisationUnit> organisationUnits = mUserAccountController.updateAssignedPrograms();
                    subscriber.onNext(organisationUnits);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
