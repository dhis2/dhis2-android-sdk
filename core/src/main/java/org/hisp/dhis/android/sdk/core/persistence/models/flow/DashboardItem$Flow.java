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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DashboardItem$Flow extends BaseIdentifiableObject$Flow {

    @Column(name = "action")
    @NotNull
    org.hisp.dhis.android.sdk.models.state.Action action;

    @Column(name = "type")
    String type;

    @Column(name = "shape")
    String shape;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "dashboard", columnType = long.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    Dashboard$Flow dashboard;

    public DashboardItem$Flow() {
        action = org.hisp.dhis.android.sdk.models.state.Action.SYNCED;
    }

    public org.hisp.dhis.android.sdk.models.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.android.sdk.models.state.Action action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Dashboard$Flow getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard$Flow dashboard) {
        this.dashboard = dashboard;
    }

    public static DashboardItem$Flow fromModel(DashboardItem dashboardItem) {
        if (dashboardItem == null) {
            return null;
        }

        DashboardItem$Flow dashboardItemFlow = new DashboardItem$Flow();
        dashboardItemFlow.setId(dashboardItem.getId());
        dashboardItemFlow.setUId(dashboardItem.getUId());
        dashboardItemFlow.setCreated(dashboardItem.getCreated());
        dashboardItemFlow.setLastUpdated(dashboardItem.getLastUpdated());
        dashboardItemFlow.setAccess(dashboardItem.getAccess());
        dashboardItemFlow.setName(dashboardItem.getName());
        dashboardItemFlow.setDisplayName(dashboardItem.getDisplayName());
        dashboardItemFlow.setDashboard(Dashboard$Flow.fromModel(dashboardItem.getDashboard()));
        dashboardItemFlow.setType(dashboardItem.getType());
        dashboardItemFlow.setShape(dashboardItem.getShape());
        dashboardItemFlow.setAction(dashboardItem.getAction());
        return dashboardItemFlow;
    }

    public static DashboardItem toModel(DashboardItem$Flow dashboardItemFlow) {
        if (dashboardItemFlow == null) {
            return null;
        }

        DashboardItem dashboardItem = new DashboardItem();
        dashboardItem.setId(dashboardItemFlow.getId());
        dashboardItem.setUId(dashboardItemFlow.getUId());
        dashboardItem.setCreated(dashboardItemFlow.getCreated());
        dashboardItem.setLastUpdated(dashboardItemFlow.getLastUpdated());
        dashboardItem.setAccess(dashboardItemFlow.getAccess());
        dashboardItem.setName(dashboardItemFlow.getName());
        dashboardItem.setDisplayName(dashboardItemFlow.getDisplayName());
        dashboardItem.setDashboard(Dashboard$Flow.toModel(dashboardItemFlow.getDashboard()));
        dashboardItem.setType(dashboardItemFlow.getType());
        dashboardItem.setShape(dashboardItemFlow.getShape());
        dashboardItem.setAction(dashboardItemFlow.getAction());
        return dashboardItem;
    }

    public static List<DashboardItem> toModels(List<DashboardItem$Flow> dashboardItemFlows) {
        List<DashboardItem> dashboardItems = new ArrayList<>();

        if (dashboardItemFlows != null && !dashboardItemFlows.isEmpty()) {
            for (DashboardItem$Flow dashboardItemFlow : dashboardItemFlows) {
                dashboardItems.add(toModel(dashboardItemFlow));
            }
        }

        return dashboardItems;
    }
}