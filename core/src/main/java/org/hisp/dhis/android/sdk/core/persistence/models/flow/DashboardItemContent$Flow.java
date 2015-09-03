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
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class DashboardItemContent$Flow extends BaseIdentifiableObject$Flow {

    @Column(name = "type")
    @NotNull
    String type;

    public DashboardItemContent$Flow() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static DashboardItemContent toModel(DashboardItemContent$Flow contentFlow) {
        if (contentFlow == null) {
            return null;
        }

        DashboardItemContent dashboardItemContent = new DashboardItemContent();
        dashboardItemContent.setId(contentFlow.getId());
        dashboardItemContent.setUId(contentFlow.getUId());
        dashboardItemContent.setCreated(contentFlow.getCreated());
        dashboardItemContent.setLastUpdated(contentFlow.getLastUpdated());
        dashboardItemContent.setName(contentFlow.getName());
        dashboardItemContent.setDisplayName(contentFlow.getDisplayName());
        return dashboardItemContent;
    }

    public static List<DashboardItemContent> toModels(List<DashboardItemContent$Flow> flows) {
        List<DashboardItemContent> dashboardItemContents = new ArrayList<>();

        if (flows != null && flows.isEmpty()) {
            for (DashboardItemContent$Flow flow : flows) {
                dashboardItemContents.add(toModel(flow));
            }
        }

        return dashboardItemContents;
    }

    public static DashboardItemContent$Flow fromModel(DashboardItemContent content) {
        if (content == null) {
            return null;
        }

        DashboardItemContent$Flow flowModel = new DashboardItemContent$Flow();
        flowModel.setId(content.getId());
        flowModel.setUId(content.getUId());
        flowModel.setCreated(content.getCreated());
        flowModel.setLastUpdated(content.getLastUpdated());
        flowModel.setName(content.getName());
        flowModel.setDisplayName(content.getDisplayName());
        return flowModel;
    }
}