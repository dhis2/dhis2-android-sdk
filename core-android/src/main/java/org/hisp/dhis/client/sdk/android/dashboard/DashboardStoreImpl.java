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

package org.hisp.dhis.client.sdk.android.dashboard;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardFlow;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardItemStore;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardStore;

import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import java.util.List;
import java.util.Set;


public class DashboardStoreImpl extends AbsIdentifiableObjectDataStore<Dashboard, DashboardFlow>
        implements DashboardStore {

    private final DashboardItemStore dashboardItemStore;
    private final TransactionManager transactionManager;

    public DashboardStoreImpl(StateStore stateStore, DashboardItemStore dashboardItemStore,
                              TransactionManager transactionManager) {
        super(DashboardFlow.MAPPER, stateStore);

        this.dashboardItemStore = dashboardItemStore;
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Dashboard> query() {
            List<DashboardFlow> dashboardFlows = new Select()
                    .from(DashboardFlow.class)
                    .queryList();
        return getMapper().mapToModels(dashboardFlows);
//        List<Dashboard> dashboards = getMapper().mapToModels(dashboardFlows);
//        return mapDashboardToDashboardItems(dashboards, dashboardItemStore.query(dashboards));
    }

    @Override
    public boolean insert(Dashboard dashboard) {
        boolean isSuccess = super.insert(dashboard);

//        if (isSuccess) {
//            saveDashboardItems(dashboard);
//        }

        return isSuccess;
    }

    @Override
    public boolean update(Dashboard dashboard) {
        boolean isSuccess = super.update(dashboard);

//        if (isSuccess) {
//            saveDashboardItems(dashboard);
//        }

        return isSuccess;
    }

    @Override
    public boolean save(Dashboard dashboard) {
        boolean isSuccess = super.save(dashboard);

//        if (isSuccess) {
//            saveDashboardItems(dashboard);
//        }

        return isSuccess;
    }

    @Override
    public Dashboard queryById(long id) {
        Dashboard dashboard = super.queryById(id);

//        List<DashboardItem> dashboardItems = dashboardItemStore.query(dashboard);
//        if (dashboard != null) {
//            dashboard.setDashboardItems(dashboardItems);
//        }

        return dashboard;
    }

    @Override
    public Dashboard queryByUid(String uid) {
        Dashboard dashboard = super.queryByUid(uid);

//        List<DashboardItem> dashboardItems = dashboardItemStore.query(dashboard);
//        if (dashboard != null) {
//            dashboard.setDashboardItems(dashboardItems);
//        }

        return dashboard;
    }

    @Override
    public List<Dashboard> queryByUids(Set<String> uids) {
        List<Dashboard> dashboards = super.queryByUids(uids);
//        return mapDashboardToDashboardItems(dashboards, dashboardItemStore
//                .query(dashboards));
        return dashboards;
    }

    @Override
    public List<Dashboard> queryAll() {
//        return mapDashboardToDashboardItems(super.queryAll(),
//                dashboardItemStore.queryAll());
        return super.queryAll();
    }
}
