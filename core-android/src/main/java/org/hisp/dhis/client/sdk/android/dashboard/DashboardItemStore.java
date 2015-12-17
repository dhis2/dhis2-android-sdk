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

package org.hisp.dhis.client.sdk.android.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.DashboardItem$Flow;
import org.hisp.dhis.client.sdk.android.flow.DashboardItem$Flow$Table;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemStore;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

import java.util.List;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class DashboardItemStore extends AbsIdentifiableObjectStore<DashboardItem, DashboardItem$Flow> implements IDashboardItemStore {

    public DashboardItemStore(IMapper<DashboardItem, DashboardItem$Flow> dashboardItemMapper) {
        super(dashboardItemMapper);
    }

    @Override
    public List<DashboardItem> queryByDashboard(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                .queryList();

        return getMapper().mapToModels(dashboardItemFlows);
    }
}
