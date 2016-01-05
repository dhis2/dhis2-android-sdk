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

package org.hisp.dhis.client.sdk.android.common;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.api.modules.NetworkModule;
import org.hisp.dhis.client.sdk.android.api.modules.PersistenceModule;
import org.hisp.dhis.client.sdk.android.api.modules.PreferencesModule;
import org.hisp.dhis.client.sdk.android.user.IUserAccountScope;
import org.hisp.dhis.client.sdk.android.user.UserAccountScope;
import org.hisp.dhis.client.sdk.core.common.controllers.ControllersModule;
import org.hisp.dhis.client.sdk.core.common.controllers.IControllersModule;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IUserPreferences;
import org.hisp.dhis.client.sdk.core.common.services.IServicesModule;
import org.hisp.dhis.client.sdk.core.common.services.ServicesModule;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import rx.Observable;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class D2 {
    private static D2 mD2;

    private final IUserPreferences mUserPreferences;
    private final IUserAccountScope mUserAccountScope;

    private D2(Context context) {
        IModelUtils modelUtils = new ModelUtils();

        IPersistenceModule persistenceModule = new PersistenceModule(context);
        IPreferencesModule preferencesModule = new PreferencesModule(context);
        INetworkModule networkModule = new NetworkModule(preferencesModule);
        IServicesModule servicesModule = new ServicesModule(persistenceModule);
        IControllersModule controllersModule = new ControllersModule(networkModule,
                persistenceModule, preferencesModule, modelUtils);

        mUserPreferences = preferencesModule.getUserPreferences();

        mUserAccountScope = new UserAccountScope(controllersModule.getUserAccountController(),
                mUserPreferences, preferencesModule.getConfigurationPreferences());
    }

    public static void init(Context context) {
        isNull(context, "Context object must not be null");

        mD2 = new D2(context);
    }

    private static D2 getInstance() {
        isNull(mD2, "You have to call init first");

        return mD2;
    }

    public static Observable<UserAccount> signIn(Configuration configuration, String username,
                                                 String password) {
        return getInstance().mUserAccountScope.signIn(configuration, username, password);
    }

    public static Observable<Boolean> isSignedIn() {
        return getInstance().mUserAccountScope.isSignedIn();
    }

    public static Observable<Boolean> signOut() {
        return getInstance().mUserAccountScope.signOut();
    }
}
