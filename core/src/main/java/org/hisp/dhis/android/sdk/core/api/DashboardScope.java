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

package org.hisp.dhis.android.sdk.core.api;

import org.hisp.dhis.android.sdk.core.controllers.common.IDataController;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardService;

public final class DashboardScope implements IDataController<Dashboard>, IDashboardService, IDashboardItemService, IDashboardElementService {
    private final IDataController<Dashboard> dataController;
    private final IDashboardService dashboardService;
    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    public DashboardScope(IDataController<Dashboard> dataController,
                          IDashboardService dashboardService,
                          IDashboardItemService dashboardItemService,
                          IDashboardElementService dashboardElementService) {
        this.dataController = dataController;
        this.dashboardService = dashboardService;
        this.dashboardItemService = dashboardItemService;
        this.dashboardElementService = dashboardElementService;
    }

    @Override
    public Dashboard createDashboard(String name) {
        return dashboardService.createDashboard(name);
    }

    @Override
    public void updateDashboardName(Dashboard dashboard, String name) {
        dashboardService.updateDashboardName(dashboard, name);
    }

    @Override
    public void deleteDashboard(Dashboard dashboard) {
        dashboardService.deleteDashboard(dashboard);
    }

    @Override
    public boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content) {
        return dashboardService.addDashboardContent(dashboard, content);
    }

    @Override
    public DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        return dashboardService.getAvailableItemByType(dashboard, type);
    }

    @Override
    public void sync() throws APIException {
        dataController.sync();
    }

    @Override
    public DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content) {
        return dashboardItemService.createDashboardItem(dashboard, content);
    }

    @Override
    public void deleteDashboardItem(DashboardItem dashboardItem) {
        dashboardItemService.deleteDashboardItem(dashboardItem);
    }

    @Override
    public int getContentCount(DashboardItem dashboardItem) {
        return dashboardItemService.getContentCount(dashboardItem);
    }

    @Override
    public DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content) {
        return dashboardElementService.createDashboardElement(item, content);
    }

    @Override
    public void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement) {
        dashboardElementService.deleteDashboardElement(dashboardItem, dashboardElement);
    }
}
