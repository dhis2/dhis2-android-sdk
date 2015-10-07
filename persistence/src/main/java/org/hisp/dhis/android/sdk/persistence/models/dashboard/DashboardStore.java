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

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardStore;
import org.hisp.dhis.android.sdk.models.common.base.IModel;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.persistence.models.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.persistence.models.flow.Dashboard$Flow;

public final class DashboardStore extends AbsIdentifiableObjectStore<Dashboard> implements IDashboardStore {
    // private final IStateStore stateStore;

    public DashboardStore() {
        super(Dashboard$Flow.class);
        // this.stateStore = stateStore;
    }

    @Override
    public <DataBaseType extends Model & IModel> DataBaseType mapToDatabaseEntity(Dashboard dashboard) {
        /* if (dashboard == null) {
            return null;
        }

        Dashboard$Flow dashboardFlow = new Dashboard$Flow();
        dashboardFlow.setId(dashboard.getId());
        dashboardFlow.setUId(dashboard.getUId());
        dashboardFlow.setCreated(dashboard.getCreated());
        dashboardFlow.setLastUpdated(dashboard.getLastUpdated());
        dashboardFlow.setAccess(dashboard.getAccess());
        dashboardFlow.setName(dashboard.getName());
        dashboardFlow.setDisplayName(dashboard.getDisplayName());
        return dashboardFlow; */
        return null;
    }

    @Override
    public <DataBaseType extends Model> Dashboard mapToModel(DataBaseType dashboardFlow) {
        /* if (dashboardFlow == null) {
            return null;
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setId(dashboardFlow.getId());
        dashboard.setUId(dashboardFlow.getUId());
        dashboard.setCreated(dashboardFlow.getCreated());
        dashboard.setLastUpdated(dashboardFlow.getLastUpdated());
        dashboard.setAccess(dashboardFlow.getAccess());
        dashboard.setName(dashboardFlow.getName());
        dashboard.setDisplayName(dashboardFlow.getDisplayName());
        return dashboard; */
        return null;
    }

    /* @Override
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
    } */
}
