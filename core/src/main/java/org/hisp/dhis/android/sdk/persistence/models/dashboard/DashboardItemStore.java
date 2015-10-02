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

package org.hisp.dhis.android.sdk.persistence.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow$Table;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;

import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardItemStore implements IDashboardItemStore {
    private final IStateStore stateStore;

    public DashboardItemStore(IStateStore stateStore) {
        this.stateStore = stateStore;
    }

    @Override
    public void insert(DashboardItem object) {
        DashboardItem$Flow dashboardItemFlow
                = DashboardItem$Flow.fromModel(object);
        dashboardItemFlow.insert();

        object.setId(dashboardItemFlow.getId());

        stateStore.saveActionForModel(object, Action.SYNCED);
    }

    @Override
    public void update(DashboardItem object) {
        DashboardItem$Flow.fromModel(object).update();
    }

    @Override
    public void save(DashboardItem object) {
        DashboardItem$Flow dashboardItemFlow
                = DashboardItem$Flow.fromModel(object);
        dashboardItemFlow.save();

        object.setId(dashboardItemFlow.getId());

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            stateStore.saveActionForModel(object, Action.SYNCED);
        }
    }

    @Override
    public void delete(DashboardItem object) {
        DashboardItem$Flow dashboardItemFlow = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardItemFlow != null) {
            dashboardItemFlow.delete();

            stateStore.deleteActionForModel(object);
        }
    }

    @Override
    public List<DashboardItem> queryAll() {
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .queryList();
        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public DashboardItem queryById(long id) {
        DashboardItem$Flow dashboardItem = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .ID).is(id))
                .querySingle();
        return DashboardItem$Flow.toModel(dashboardItem);
    }

    @Override
    public DashboardItem queryByUid(String uid) {
        DashboardItem$Flow dashboardItem = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return DashboardItem$Flow.toModel(dashboardItem);
    }

    @Override
    public List<DashboardItem> queryByDashboard(Dashboard dashboard) {
        isNull(dashboard, "Dashboard must not be null");

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    /* @Override
    public List<DashboardItem> filterByType(Dashboard dashboard, String type) {
        isNull(dashboard, "Dashboard object must not be null");

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardItem$Flow$Table
                                .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                        .and(Condition.column(DashboardItem$Flow$Table
                                .TYPE).isNot(type)))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    } */
}
