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
import com.raizlabs.android.dbflow.sql.builder.Condition.CombinedCondition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow$Table;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardStore;

import java.util.Arrays;
import java.util.List;

public final class DashboardStore implements IDashboardStore {

    @Override
    public void insert(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.insert();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void update(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.update();
    }

    @Override
    public void save(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.save();

        object.setId(dashboardFlow.getId());
    }

    @Override
    public void delete(Dashboard object) {
        Dashboard$Flow dashboardFlow = new Select().from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardFlow != null) {
            dashboardFlow.delete();
        }
    }

    @Override
    public List<Dashboard> query() {
        List<Dashboard$Flow> dashboardFlows =
                new Select().from(Dashboard$Flow.class).queryList();
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    @Override
    public Dashboard query(long id) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(id))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
    }

    @Override
    public Dashboard query(String uid) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
    }

    @Override
    public List<Dashboard> query(Action... actions) {
        return query(Arrays.asList(actions));
    }

    @Override
    public List<Dashboard> query(List<Action> actions) {
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException("Please, provide at least one Action");
        }

        CombinedCondition combinedCondition = null;
        for (Action action : actions) {
            if (combinedCondition == null) {
                combinedCondition = CombinedCondition.begin(isState(action));
            } else {
                combinedCondition = combinedCondition.or(isState(action));
            }
        }

        List<Dashboard$Flow> dashboardFlows = new Select()
                .from(Dashboard$Flow.class)
                .where(combinedCondition)
                .queryList();

        // converting flow models to Dashboard
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    @Override
    public List<Dashboard> filter(Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Please, provide Action");
        }

        List<Dashboard$Flow> dashboardFlows = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ACTION).isNot(action.toString()))
                .queryList();

        // converting flow models to Dashboard
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    private static Condition isState(Action action) {
        return Condition.column(Dashboard$Flow$Table
                .ACTION).is(action.toString());
    }
}
