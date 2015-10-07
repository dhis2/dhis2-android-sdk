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

import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.persistence.models.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardElement$Flow;

public class DashboardElementMapper extends AbsMapper<DashboardElement, DashboardElement$Flow> {
    private final DashboardItemMapper dashboardItemMapper;

    public DashboardElementMapper(DashboardItemMapper dashboardItemMapper) {
        this.dashboardItemMapper = dashboardItemMapper;
    }

    @Override
    public DashboardElement$Flow mapToDatabaseEntity(DashboardElement dashboardElement) {
        if (dashboardElement == null) {
            return null;
        }

        DashboardElement$Flow dashboardElementFlow = new DashboardElement$Flow();
        dashboardElementFlow.setId(dashboardElement.getId());
        dashboardElementFlow.setUId(dashboardElement.getUId());
        dashboardElementFlow.setCreated(dashboardElement.getCreated());
        dashboardElementFlow.setLastUpdated(dashboardElement.getLastUpdated());
        dashboardElementFlow.setAccess(dashboardElement.getAccess());
        dashboardElementFlow.setName(dashboardElement.getName());
        dashboardElementFlow.setDisplayName(dashboardElement.getDisplayName());
        dashboardElementFlow.setDashboardItem(dashboardItemMapper
                .mapToDatabaseEntity(dashboardElement.getDashboardItem()));
        return dashboardElementFlow;
    }

    @Override
    public DashboardElement mapToModel(DashboardElement$Flow dashboardElementFlow) {
        if (dashboardElementFlow == null) {
            return null;
        }

        DashboardElement dashboardElement = new DashboardElement();
        dashboardElement.setId(dashboardElementFlow.getId());
        dashboardElement.setUId(dashboardElementFlow.getUId());
        dashboardElement.setCreated(dashboardElementFlow.getCreated());
        dashboardElement.setLastUpdated(dashboardElementFlow.getLastUpdated());
        dashboardElement.setAccess(dashboardElementFlow.getAccess());
        dashboardElement.setName(dashboardElementFlow.getName());
        dashboardElement.setDisplayName(dashboardElementFlow.getDisplayName());
        dashboardElement.setDashboardItem(dashboardItemMapper
                .mapToModel(dashboardElementFlow.getDashboardItem()));
        return dashboardElement;
    }

    @Override
    public Class<DashboardElement> getModelTypeClass() {
        return DashboardElement.class;
    }

    @Override
    public Class<DashboardElement$Flow> getDatabaseEntityTypeClass() {
        return DashboardElement$Flow.class;
    }
}
