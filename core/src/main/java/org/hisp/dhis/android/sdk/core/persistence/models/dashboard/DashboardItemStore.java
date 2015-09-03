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

package org.hisp.dhis.android.sdk.core.persistence.models.dashboard;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.DashboardItem$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;

import java.util.Arrays;
import java.util.List;

public class DashboardItemStore implements IDashboardItemStore {

    @Override
    public void insert(DashboardItem object) {
        DashboardItem$Flow dashboardItemFlow
                = DashboardItem$Flow.fromModel(object);
        dashboardItemFlow.insert();

        object.setId(dashboardItemFlow.getId());
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
    }

    @Override
    public void delete(DashboardItem object) {
        DashboardItem$Flow.fromModel(object).delete();
    }

    @Override
    public List<DashboardItem> query() {
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .queryList();
        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public DashboardItem query(long id) {
        DashboardItem$Flow dashboardItem = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .ID).is(id))
                .querySingle();
        return DashboardItem$Flow.toModel(dashboardItem);
    }

    @Override
    public DashboardItem query(String uid) {
        DashboardItem$Flow dashboardItem = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return DashboardItem$Flow.toModel(dashboardItem);
    }

    @Override
    public List<DashboardItem> query(State... states) {
        return query(Arrays.asList(states));
    }

    @Override
    public List<DashboardItem> query(List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> query(Dashboard dashboard, List<State> states) {
        if (states == null || states.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one State");
        }

        Condition.CombinedCondition combinedCondition = buildCombinedCondition(states);
        combinedCondition = combinedCondition.and(Condition.column(DashboardItem$Flow$Table
                .DASHBOARD_DASHBOARD).is(dashboard.getId()));
        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(combinedCondition)
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.column(DashboardItem$Flow$Table
                        .STATE).isNot(state.toString()))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(Dashboard dashboard, State state) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardItem$Flow$Table
                                .STATE).isNot(state.toString()))
                        .and(Condition.column(DashboardItem$Flow$Table
                                .DASHBOARD_DASHBOARD).is(dashboard.getId())))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    @Override
    public List<DashboardItem> filter(Dashboard dashboard, State state, String type) {
        if (state == null) {
            throw new IllegalArgumentException("Please, provide State");
        }

        List<DashboardItem$Flow> dashboardItemFlows = new Select()
                .from(DashboardItem$Flow.class)
                .where(Condition.CombinedCondition
                        .begin(Condition.column(DashboardItem$Flow$Table
                                .STATE).isNot(state.toString()))
                        .and(Condition.column(DashboardItem$Flow$Table
                                .DASHBOARD_DASHBOARD).is(dashboard.getId()))
                        .and(Condition.column(DashboardItem$Flow$Table.TYPE).isNot(type)))
                .queryList();

        return DashboardItem$Flow.toModels(dashboardItemFlows);
    }

    private static Condition.CombinedCondition buildCombinedCondition(List<State> states) {
        Condition.CombinedCondition combinedCondition = null;
        for (State state : states) {
            if (combinedCondition == null) {
                combinedCondition = Condition.CombinedCondition.begin(isState(state));
            } else {
                combinedCondition = combinedCondition.or(isState(state));
            }
        }
        return combinedCondition;
    }

    private static Condition isState(State state) {
        return Condition.column(DashboardItem$Flow$Table
                .STATE).is(state.toString());
    }
}
