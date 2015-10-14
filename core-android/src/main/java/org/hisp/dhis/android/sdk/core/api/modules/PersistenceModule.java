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

package org.hisp.dhis.android.sdk.core.api.modules;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.sdk.corejava.common.modules.IPersistenceModule;
import org.hisp.dhis.android.sdk.corejava.common.persistence.ITransactionManager;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardStore;
import org.hisp.dhis.android.sdk.corejava.common.IStateStore;
import org.hisp.dhis.android.sdk.core.common.state.StateStore;
import org.hisp.dhis.android.sdk.core.dashboard.DashboardElementStore;
import org.hisp.dhis.android.sdk.core.dashboard.DashboardContentStore;
import org.hisp.dhis.android.sdk.core.dashboard.DashboardItemStore;
import org.hisp.dhis.android.sdk.core.dashboard.DashboardStore;

public class PersistenceModule implements IPersistenceModule {
    private final IStateStore stateStore;
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;
    private final ITransactionManager transactionManager;

    public PersistenceModule(Context context) {
        FlowManager.init(context);

        stateStore = new StateStore(null, null, null, null);
        dashboardStore = new DashboardStore(null);
        dashboardItemStore = new DashboardItemStore(null);
        dashboardElementStore = new DashboardElementStore(null);
        dashboardItemContentStore = new DashboardContentStore(null);
        transactionManager = new TransactionManager();
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public IStateStore getStateStore() {
        return stateStore;
    }

    @Override
    public IDashboardStore getDashboardStore() {
        return dashboardStore;
    }

    @Override
    public IDashboardItemStore getDashboardItemStore() {
        return dashboardItemStore;
    }

    @Override
    public IDashboardElementStore getDashboardElementStore() {
        return dashboardElementStore;
    }

    @Override
    public IDashboardItemContentStore getDashboardContentStore() {
        return dashboardItemContentStore;
    }
}
