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
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;

@Table(database = DbDhis.class)
public final class DashboardContentFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<DashboardContent, DashboardContentFlow> MAPPER = new ContentMapper();

    @Column(name = "type")
    @NotNull
    String type;

    public DashboardContentFlow() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private static class ContentMapper extends AbsMapper<DashboardContent, DashboardContentFlow> {

        @Override
        public DashboardContentFlow mapToDatabaseEntity(DashboardContent content) {
            if (content == null) {
                return null;
            }

            DashboardContentFlow flowModel = new DashboardContentFlow();
            flowModel.setId(content.getId());
            flowModel.setUId(content.getUId());
            flowModel.setCreated(content.getCreated());
            flowModel.setLastUpdated(content.getLastUpdated());
            flowModel.setName(content.getName());
            flowModel.setDisplayName(content.getDisplayName());
            flowModel.setType(content.getType());
            return flowModel;
        }

        @Override
        public DashboardContent mapToModel(DashboardContentFlow contentFlow) {
            if (contentFlow == null) {
                return null;
            }

            DashboardContent dashboardContent = new DashboardContent();
            dashboardContent.setId(contentFlow.getId());
            dashboardContent.setUId(contentFlow.getUId());
            dashboardContent.setCreated(contentFlow.getCreated());
            dashboardContent.setLastUpdated(contentFlow.getLastUpdated());
            dashboardContent.setName(contentFlow.getName());
            dashboardContent.setDisplayName(contentFlow.getDisplayName());
            dashboardContent.setType(contentFlow.getType());
            return dashboardContent;
        }

        @Override
        public Class<DashboardContent> getModelTypeClass() {
            return DashboardContent.class;
        }

        @Override
        public Class<DashboardContentFlow> getDatabaseEntityTypeClass() {
            return DashboardContentFlow.class;
        }
    }
}