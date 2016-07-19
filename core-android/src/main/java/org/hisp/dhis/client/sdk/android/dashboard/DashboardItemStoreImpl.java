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

import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardItemStore;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class DashboardItemStoreImpl extends AbsIdentifiableObjectDataStore<DashboardItem,
        DashboardItemFlow> implements DashboardItemStore {

    public DashboardItemStoreImpl(StateStore stateStore) {
        super(DashboardItemFlow.MAPPER, stateStore);
    }

    @Override
    public List<DashboardItem> query(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

//        List<DashboardItem_Flow> dashboardItemFlows = new Select()
//                .from(DashboardItem_Flow.class)
//                .where(DashboardItem_Flow_Table.dashboard.is(dashboard.getId()))
//                .queryList();
//
//        return getMapper().mapToModels(dashboardItemFlows);
        return null;
    }

    @Override
    public List<DashboardItem> query(String uId) {
        isNull(uId, "uId must not be null");

        List<DashboardItemFlow> dashboardItemFlows = new Select()
                .from(DashboardItemFlow.class)
                .where(DashboardItemFlow_Table.dashboard.is(uId))
                .and(DashboardItemFlow_Table.type.isNot(DashboardContent.TYPE_MESSAGES))
                .queryList();

        return getMapper().mapToModels(dashboardItemFlows);
    }
}
