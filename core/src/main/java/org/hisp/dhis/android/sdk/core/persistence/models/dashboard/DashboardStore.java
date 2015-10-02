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

import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.Dashboard$Flow$Table;
import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;

import java.util.List;

public final class DashboardStore implements IIdentifiableObjectStore<Dashboard> {
    private final IStateStore stateStore;

    public DashboardStore(IStateStore stateStore) {
        this.stateStore = stateStore;
    }

    @Override
    public void insert(Dashboard object) {
        Dashboard$Flow dashboardFlow
                = Dashboard$Flow.fromModel(object);
        dashboardFlow.insert();

        object.setId(dashboardFlow.getId());

        stateStore.saveActionForModel(object, Action.SYNCED);
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

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            stateStore.saveActionForModel(object, Action.SYNCED);
        }
    }

    @Override
    public void delete(Dashboard object) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(object.getId()))
                .querySingle();

        if (dashboardFlow != null) {
            dashboardFlow.delete();

            stateStore.deleteActionForModel(object);
        }
    }

    @Override
    public List<Dashboard> queryAll() {
        List<Dashboard$Flow> dashboardFlows =
                new Select().from(Dashboard$Flow.class).queryList();
        return Dashboard$Flow.toModels(dashboardFlows);
    }

    @Override
    public Dashboard queryById(long id) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .ID).is(id))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
    }

    @Override
    public Dashboard queryByUid(String uid) {
        Dashboard$Flow dashboardFlow = new Select()
                .from(Dashboard$Flow.class)
                .where(Condition.column(Dashboard$Flow$Table
                        .UID).is(uid))
                .querySingle();
        return Dashboard$Flow.toModel(dashboardFlow);
    }
}
