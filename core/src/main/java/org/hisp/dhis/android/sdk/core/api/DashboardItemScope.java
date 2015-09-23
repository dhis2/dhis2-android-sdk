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

import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DashboardItemScope implements IDashboardItemService, IDashboardItemStore {
    private final IDashboardItemService dashboardItemService;
    private final IDashboardItemStore dashboardItemStore;
    private final IStateStore stateStore;

    public DashboardItemScope(IDashboardItemService dashboardItemService,
                              IDashboardItemStore dashboardItemStore,
                              IStateStore stateStore) {
        this.dashboardItemService = dashboardItemService;
        this.dashboardItemStore = dashboardItemStore;
        this.stateStore = stateStore;
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
    public List<DashboardItem> query(Dashboard dashboard) {
        List<DashboardItem> dashboardItems = dashboardItemStore.query(dashboard);
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardItem.class);

        List<DashboardItem> filteredItems = new ArrayList<>();
        if (dashboardItems != null && !dashboardItems.isEmpty()) {
            for (DashboardItem dashboardItem : dashboardItems) {
                if (!Action.TO_DELETE.equals(actionMap.get(dashboardItem.getId()))) {
                    filteredItems.add(dashboardItem);
                }
            }
        }

        return filteredItems;
    }

    @Override
    public List<DashboardItem> filterByType(Dashboard dashboard, String type) {
        List<DashboardItem> dashboardItems = dashboardItemStore.filterByType(dashboard, type);
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardItem.class);

        List<DashboardItem> filteredItems = new ArrayList<>();
        if (dashboardItems != null && !dashboardItems.isEmpty()) {
            for (DashboardItem dashboardItem : dashboardItems) {
                if (!Action.TO_DELETE.equals(actionMap.get(dashboardItem.getId()))) {
                    filteredItems.add(dashboardItem);
                }
            }
        }

        return filteredItems;
    }

    @Override
    public DashboardItem query(long id) {
        DashboardItem dashboardItem = dashboardItemStore.query(id);

        if (dashboardItem != null) {
            Action action = stateStore.queryAction(dashboardItem);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboardItem;
            }
        }

        return null;
    }

    @Override
    public DashboardItem query(String uid) {
        DashboardItem dashboardItem = dashboardItemStore.query(uid);

        if (dashboardItem != null) {
            Action action = stateStore.queryAction(dashboardItem);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboardItem;
            }
        }

        return null;
    }

    @Override
    public void insert(DashboardItem object) {

    }

    @Override
    public void update(DashboardItem object) {

    }

    @Override
    public void save(DashboardItem object) {

    }

    @Override
    public void delete(DashboardItem object) {

    }

    @Override
    public List<DashboardItem> query() {
        return null;
    }
}
