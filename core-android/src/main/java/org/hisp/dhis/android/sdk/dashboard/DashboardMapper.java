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

package org.hisp.dhis.android.sdk.dashboard;

import org.hisp.dhis.java.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.flow.Dashboard$Flow;

public class DashboardMapper extends AbsMapper<Dashboard, Dashboard$Flow> {

    public DashboardMapper() {
        // empty constructor
    }

    @Override
    public Dashboard$Flow mapToDatabaseEntity(Dashboard dashboard) {
        if (dashboard == null) {
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
        return dashboardFlow;
    }

    @Override
    public Dashboard mapToModel(Dashboard$Flow dashboardFlow) {
        if (dashboardFlow == null) {
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
        return dashboard;
    }

    @Override
    public Class<Dashboard> getModelTypeClass() {
        return Dashboard.class;
    }

    @Override
    public Class<Dashboard$Flow> getDatabaseEntityTypeClass() {
        return Dashboard$Flow.class;
    }
}
