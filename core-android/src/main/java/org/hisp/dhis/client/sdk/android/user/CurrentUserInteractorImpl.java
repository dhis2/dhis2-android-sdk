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


import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.android.program.UserProgramInteractor;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;
import org.hisp.dhis.client.sdk.core.common.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.UserPreferences;
import org.hisp.dhis.client.sdk.core.user.UserAccountController;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

import rx.Observable;

public class CurrentUserInteractorImpl implements CurrentUserInteractor {
    // preferences
    private final UserPreferences userPreferences;

    // services
    private final UserAccountService userAccountService;

    // controllers
    private final UserAccountController userAccountController;

    // interactors
    private final UserAccountInteractor userAccountInteractor;
    private final UserProgramInteractor userProgramInteractor;
    private final UserOrganisationUnitInteractor organisationUnitInteractor;

    //modules
    private final PersistenceModule persistanceModule;
    private final PreferencesModule preferencesModule;

    public CurrentUserInteractorImpl(UserPreferences userPreferences,
                                     UserAccountService userAccountService,
                                     UserAccountController userAccountController,
                                     UserAccountInteractor userAccountInteractor,
                                     UserProgramInteractor userProgramInteractor,
                                     UserOrganisationUnitInteractor organisationUnitInteractor,
                                     PreferencesModule preferencesModule,
                                     PersistenceModule persitenceModule) {
        this.userPreferences = userPreferences;
        this.userAccountService = userAccountService;
        this.userAccountController = userAccountController;
        this.userAccountInteractor = userAccountInteractor;
        this.userProgramInteractor = userProgramInteractor;
        this.organisationUnitInteractor = organisationUnitInteractor;
        this.persistanceModule = persitenceModule;
        this.preferencesModule = preferencesModule;
    }

    @Override
    public Observable<UserAccount> signIn(final String username, final String password) {
        return Observable.create(new DefaultOnSubscribe<UserAccount>() {
            @Override
            public UserAccount call() {
                if (userPreferences.isUserConfirmed()) {
                    throw new IllegalArgumentException("User is already signed in");
                }

                UserCredentials userCredentials = new UserCredentials(username, password);
                userPreferences.save(userCredentials);

                userAccountController.pull();
                return userAccountService.get();
            }
        });
    }

    @Override
    public Observable<Boolean> isSignedIn() {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return userPreferences.isUserConfirmed();
            }
        });
    }

    @Override
    public Observable<Boolean> signOut() {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return userPreferences.clear() &&
                        preferencesModule.clearAllPreferences() &&
                        persistanceModule.deleteAllTables();
            }
        });
    }

    @Override
    public Observable<UserCredentials> userCredentials() {
        return Observable.create(new DefaultOnSubscribe<UserCredentials>() {
            @Override
            public UserCredentials call() {
                return userPreferences.get();
            }
        });
    }

    @Override
    public UserAccountInteractor account() {
        return userAccountInteractor;
    }

    @Override
    public UserProgramInteractor programs() {
        return userProgramInteractor;
    }

    @Override
    public UserOrganisationUnitInteractor organisationUnits() {
        return organisationUnitInteractor;
    }
}
