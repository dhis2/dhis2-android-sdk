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


import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitScope;
import org.hisp.dhis.client.sdk.android.program.UserProgramScope;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.preferences.UserPreferences;
import org.hisp.dhis.client.sdk.core.user.UserAccountController;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;
import rx.Subscriber;

public class UserAccountScopeImpl implements UserAccountScope {
    // preferences
    private final UserPreferences userPreferences;

    // services
    private final UserAccountService userAccountService;

    // controllers
    private final UserAccountController userAccountController;

    // scopes
    private final UserProgramScope userProgramScope;
    private final UserOrganisationUnitScope organisationUnitScope;

    public UserAccountScopeImpl(UserPreferences userPreferences,
                                UserAccountService userAccountService,
                                UserAccountController userAccountController,
                                UserProgramScope userProgramScope,
                                UserOrganisationUnitScope organisationUnitScope) {
        this.userPreferences = userPreferences;
        this.userAccountService = userAccountService;
        this.userAccountController = userAccountController;
        this.userProgramScope = userProgramScope;
        this.organisationUnitScope = organisationUnitScope;
    }

    @Override
    public Observable<UserAccount> signIn(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<UserAccount>() {

            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    if (userPreferences.isUserConfirmed()) {
                        throw new IllegalArgumentException("User is already signed in");
                    }

                    UserCredentials userCredentials = new UserCredentials(username, password);
                    userPreferences.save(userCredentials);

                    UserAccount userAccount = userAccountController.updateAccount();
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
                    if (userPreferences.isUserConfirmed()) {
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
                    subscriber.onNext(userPreferences.clear());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<UserAccount> account() {
        return Observable.create(new Observable.OnSubscribe<UserAccount>() {

            @Override
            public void call(Subscriber<? super UserAccount> subscriber) {
                try {
                    subscriber.onNext(userAccountService.getCurrentUserAccount());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<UserCredentials> userCredentials() {
        return Observable.create(new Observable.OnSubscribe<UserCredentials>() {
            @Override
            public void call(Subscriber<? super UserCredentials> subscriber) {
                try {
                    subscriber.onNext(userPreferences.get());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final UserAccount userAccount) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    subscriber.onNext(userAccountService.save(userAccount));
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public UserProgramScope programs() {
        return userProgramScope;
    }

    @Override
    public UserOrganisationUnitScope organisationUnits() {
        return organisationUnitScope;
    }
}
