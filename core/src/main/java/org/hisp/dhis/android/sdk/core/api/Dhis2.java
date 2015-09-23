/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.DhisApi;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.network.RepositoryManager;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.Credentials;
import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.Session;
import org.hisp.dhis.android.sdk.core.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.persistence.preferences.LastUpdatedManager;
import org.hisp.dhis.android.sdk.models.user.UserAccount;

public final class Dhis2 {
    // Reference to Dhis2 instance
    private static Dhis2 dhis2;

    // Current session information
    private final DhisApi dhisApi;

    private final DashboardScope dashboardScope;
    private final DashboardItemScope dashboardItemScope;
    private final InterpretationScope interpretationScope;
    private final UserAccountScope userAccountScope;

    private Dhis2(Context context) {
        dhisApi = RepositoryManager.createService();

        Models.init(context);
        Services.init(context);
        Controllers.init(dhisApi.getApi());

        LastUpdatedManager.init(context);
        DateTimeManager.init(context);

        dashboardScope = new DashboardScope(Controllers.dashboards(), Services.dashboards());
        dashboardItemScope = new DashboardItemScope(Services.dashboardItems(),
                Models.dashboardItems(), Models.stateStore());
        interpretationScope = new InterpretationScope(Controllers.interpretations(),
                Services.interpretations(), Services.interpretationElements(), Services.interpretationComments());
        userAccountScope = new UserAccountScope(Controllers.userAccount(), Services.userAccount());

        readSession();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Dhis2 API public methods.
    ////////////////////////////////////////////////////////////////////////////////////////

    public static void init(Context context) {
        if (dhis2 == null) {
            dhis2 = new Dhis2(context);
        }
    }

    public static UserAccount logIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        return getInstance().signIn(serverUrl, credentials);
    }

    public static void logOut() throws APIException {

        getInstance().userAccountScope.logOut();

        // fetch meta data from disk
        getInstance().readSession();
    }

    public static UserAccount confirmUser(Credentials credentials) throws APIException {
        return getInstance().signIn(getServerUrl(), credentials);
    }

    public static void invalidateSession() {
        LastUpdatedManager.getInstance().invalidate();

        // fetch meta data from disk
        getInstance().readSession();
    }

    public static UserAccount getCurrentUserAccount() {
        return getInstance().userAccountScope.getCurrentUserAccount();
    }

    public static boolean isUserLoggedIn() {
        return isUserLoggedIn(getSession());
    }

    public static boolean isUserInvalidated() {
        return getServerUrl() != null && getUserCredentials() == null;
    }

    @Nullable
    public static HttpUrl getServerUrl() {
        return getSession().getServerUrl();
    }

    @Nullable
    public static Credentials getUserCredentials() {
        return getSession().getCredentials();
    }

    public static IDhisApi getServiceApi() {
        return getInstance().dhisApi.getApi();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Utility methods.
    ////////////////////////////////////////////////////////////////////////////////////////

    private static Dhis2 getInstance() {
        if (dhis2 == null) {
            throw new IllegalArgumentException("You have to call init() method first.");
        }

        return dhis2;
    }

    private void readSession() {
        dhisApi.setSession(null);

        Session session = LastUpdatedManager.getInstance().get();
        if (isUserLoggedIn(session)) {
            dhisApi.setSession(session);
        }
    }

    private static Session getSession() {
        Session session = getInstance().dhisApi.getSession();
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    private UserAccount signIn(HttpUrl serverUrl, Credentials credentials) throws APIException {
        dhisApi.setSession(new Session(serverUrl, credentials));
        UserAccount user = userAccountScope.logIn(serverUrl, credentials);

        // fetch meta data from disk
        readSession();
        return user;
    }

    private static boolean isUserLoggedIn(Session session) {
        return session.getServerUrl() != null && session.getCredentials() != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Scopes
    ////////////////////////////////////////////////////////////////////////////////////////

    public static DashboardScope dashboards() {
        return getInstance().dashboardScope;
    }

    public static DashboardItemScope dashboarditems() {
        return getInstance().dashboardItemScope;
    }

    public static InterpretationScope interpretations() {
        return getInstance().interpretationScope;
    }
}
