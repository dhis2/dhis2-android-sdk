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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;

@Table(database = DbDhis.class)
public final class DashboardItemFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<DashboardItem, DashboardItemFlow> MAPPER = new ItemMapper();

    @Column(name = "type")
    String type;

    @Column(name = "shape")
    String shape;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = "dashboard", columnType = String.class,
                            foreignKeyColumnName = BaseIdentifiableObjectFlow.COLUMN_UID)
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DashboardFlow dashboard;

    public DashboardItemFlow() {
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

    public DashboardFlow getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardFlow dashboard) {
        this.dashboard = dashboard;
    }

    private static class ItemMapper extends AbsMapper<DashboardItem, DashboardItemFlow> {

        @Override
        public DashboardItemFlow mapToDatabaseEntity(DashboardItem dashboardItem) {
            if (dashboardItem == null) {
                return null;
            }

            DashboardItemFlow dashboardItemFlow = new DashboardItemFlow();
            dashboardItemFlow.setId(dashboardItem.getId());
            dashboardItemFlow.setUId(dashboardItem.getUId());
            dashboardItemFlow.setCreated(dashboardItem.getCreated());
            dashboardItemFlow.setLastUpdated(dashboardItem.getLastUpdated());
            dashboardItemFlow.setAccess(dashboardItem.getAccess());
            dashboardItemFlow.setName(dashboardItem.getName());
            dashboardItemFlow.setDisplayName(dashboardItem.getDisplayName());
            dashboardItemFlow.setDashboard(DashboardFlow.MAPPER
                    .mapToDatabaseEntity(dashboardItem.getDashboard()));
            dashboardItemFlow.setType(dashboardItem.getType());
            dashboardItemFlow.setShape(dashboardItem.getShape());
            return dashboardItemFlow;
        }

        @Override
        public DashboardItem mapToModel(DashboardItemFlow dashboardItemFlow) {
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
            dashboardItem.setDashboard(DashboardFlow.MAPPER
                    .mapToModel(dashboardItemFlow.getDashboard()));
            dashboardItem.setType(dashboardItemFlow.getType());
            dashboardItem.setShape(dashboardItemFlow.getShape());
            return dashboardItem;
        }

        @Override
        public Class<DashboardItem> getModelTypeClass() {
            return DashboardItem.class;
        }

        @Override
        public Class<DashboardItemFlow> getDatabaseEntityTypeClass() {
            return DashboardItemFlow.class;
        }
    }
}