/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.user;


import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.IConfigurationPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.user.IAssignedProgramsController;
import org.hisp.dhis.client.sdk.core.user.IUserAccountController;
import org.hisp.dhis.client.sdk.core.user.IUserAccountStore;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class UserAccountScope implements IUserAccountScope {
    private final IUserAccountController mUserAccountController;
    private final IUserPreferences mUserPreferences;
    private final IConfigurationPreferences mConfigurationPreferences;
    private final IAssignedProgramsController mAssignedProgramsController;
    private final IUserAccountStore mUserAccountStore;

    public UserAccountScope(IUserAccountController userAccountController,
                            IUserPreferences userPreferences,
                            IConfigurationPreferences configurationPreferences, IAssignedProgramsController mAssignedProgramsController,
                            IUserAccountStore userAccountStore) {
        mUserAccountController = userAccountController;
        mUserPreferences = userPreferences;
        mConfigurationPreferences = configurationPreferences;
        this.mAssignedProgramsController = mAssignedProgramsController;
        mUserAccountStore = userAccountStore;
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
                    if (mUserPreferences.isUserConfirmed()) {
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

    @Override

    public void syncAssignedPrograms() {
        mAssignedProgramsController.sync();
    }

    public Observable<UserAccount> account() {
        return Observable.create(new Observable.OnSubscribe<UserAccount>() {
            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    List<UserAccount> userAccounts = mUserAccountStore.queryAll();
                    if (userAccounts != null && !userAccounts.isEmpty()) {
                        for (UserAccount userAccount : userAccounts) {
                            subscriber.onNext(userAccount);
                        }
                    }
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
