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

package org.hisp.dhis.android.sdk.api.api;

import android.content.Context;

import org.hisp.dhis.android.sdk.corejava.common.modules.ControllersModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.IControllersModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.INetworkModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.IPersistenceModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.IPreferencesModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.IServicesModule;
import org.hisp.dhis.android.sdk.corejava.common.modules.ServicesModule;
import org.hisp.dhis.android.sdk.network.modules.NetworkModule;
import org.hisp.dhis.android.sdk.persistence.api.PersistenceModule;
import org.hisp.dhis.android.sdk.persistence.api.PreferencesModule;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public final class Dhis2 {
    // singleton instance
    private static Dhis2 mDhis2;

    // network module
    private final INetworkModule mNetworkModule;

    // persistence module
    private final IPersistenceModule mPersistenceModule;

    // preferences module
    private final IPreferencesModule mPreferencesModule;

    private final IControllersModule mControllersModule;
    private final IServicesModule mServicesModule;

    private final DashboardScope mDashboardScope;

    private Dhis2(INetworkModule networkModule, IPersistenceModule persistenceModule, IPreferencesModule preferencesModule) {
        mNetworkModule = isNull(networkModule, "networkModule must not be null");
        mPersistenceModule = isNull(persistenceModule, "persistenceModule must not be null");
        mPreferencesModule = isNull(preferencesModule, "preferencesModule must not be null");

        mControllersModule = new ControllersModule(networkModule, persistenceModule, preferencesModule);
        mServicesModule = new ServicesModule(persistenceModule);

        mDashboardScope = new DashboardScope(mControllersModule.getDashboardController(), mServicesModule.getDashboardService());
    }

    public static void init(Context context) {
        if (mDhis2 == null) {
            IPersistenceModule persistenceModule = new PersistenceModule(context);
            INetworkModule networkModule = new NetworkModule();
            IPreferencesModule preferencesModule = new PreferencesModule(context);
            mDhis2 = new Dhis2(networkModule, persistenceModule, preferencesModule);
        }
    }

    public static Dhis2 getInstance() {
        if (mDhis2 == null) {
            throw new IllegalArgumentException("You have to call init first");
        }

        return mDhis2;
    }

    public static DashboardScope getDashboardScope() {
        return getInstance().mDashboardScope;
    }

}
