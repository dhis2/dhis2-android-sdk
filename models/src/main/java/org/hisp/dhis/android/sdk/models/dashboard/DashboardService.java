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

package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardService implements IDashboardService {
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    public DashboardService(IDashboardStore dashboardStore, IDashboardItemStore dashboardItemStore,
                            IDashboardElementStore dashboardElementStore,
                            IDashboardItemService dashboardItemService, IDashboardElementService dashboardElementService) {
        this.dashboardStore = dashboardStore;
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemService = dashboardItemService;
        this.dashboardElementService = dashboardElementService;
    }

    /**
     * Factory method which creates new Dashboard with given name.
     *
     * @param name Name of new dashboard.
     * @return a dashboard.
     */
    @Override
    public Dashboard createDashboard(String name) {
        DateTime lastUpdated = new DateTime();

        Dashboard dashboard = new Dashboard();
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(lastUpdated);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setAccess(Access.createDefaultAccess());
        dashboard.setState(State.TO_POST);
        return dashboard;
    }

    /**
     * @param dashboard to be removed.
     * @throws IllegalArgumentException in cases when dashboard is null.
     */
    @Override
    public void deleteDashboard(Dashboard dashboard) {
        isNull(dashboard, "dashboard argument must not be null");

        if (State.TO_DELETE.equals(dashboard.getState())) {
            dashboard.setState(State.TO_DELETE);
            dashboardStore.delete(dashboard);
        } else {
            dashboard.setState(State.TO_DELETE);
            dashboardStore.update(dashboard);
        }
    }

    /**
     * Changes the name of dashboard along with the State.
     * <p/>
     * If the current state of model is State.TO_DELETE or State.TO_POST,
     * state won't be changed. Otherwise, it will be set to State.TO_UPDATE.
     *
     * @param name Name for dashboard.
     * @throws IllegalArgumentException in cases when dashboard is null.
     */
    @Override
    public void updateDashboardName(Dashboard dashboard, String name) {
        isNull(dashboard, "dashboard argument must not be null");

        if (State.TO_DELETE.equals(dashboard.getState())) {
            throw new IllegalArgumentException("The name of dashboard with State." +
                    "TO_DELETE cannot be updated");
        }

        /* if dashboard was not posted to the server before,
        you don't have anything to update */
        if (dashboard.getState() != State.TO_POST) {
            dashboard.setState(State.TO_UPDATE);
        }

        dashboard.setName(name);
        dashboard.setDisplayName(name);

        dashboardStore.update(dashboard);
    }

    /**
     * Will try to append DashboardItemContent to current dashboard.
     * If the type of DashboardItemContent is embedded (chart, eventChart, map, eventReport, reportTable),
     * method will create a new item and append it to dashboard.
     * <p/>
     * If the type of DashboardItemContent is link type (users, reports, resources),
     * method will try to append content to existing item. Otherwise it will create a new dashboard item.
     * <p/>
     * If the overall count of items in dashboard is bigger that Dashboard.MAX_ITEMS, method will not
     * add content and return false;
     *
     * @param dashboard dashboard to which we want add new content.
     * @param content   content which we want to add to given dashboard.
     * @return false if item count is bigger than MAX_ITEMS.
     * @throws IllegalArgumentException if dashboard or content is null.
     */
    @Override
    public boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content) {
        isNull(dashboard, "Dashboard object must not be null");
        isNull(content, "DashboardItemContent object must not be null");

        DashboardItem item;
        DashboardElement element;
        int itemsCount = getDashboardItemCount(dashboard);

        if (isItemContentTypeEmbedded(content)) {
            item = dashboardItemService.createDashboardItem(dashboard, content);
            element = dashboardElementService.createDashboardElement(item, content);
            itemsCount += 1;
        } else {
            item = getAvailableItemByType(dashboard, content.getType());
            if (item == null) {
                item = dashboardItemService.createDashboardItem(dashboard, content);
                itemsCount += 1;
            }
            element = dashboardElementService.createDashboardElement(item, content);
        }

        if (itemsCount > Dashboard.MAX_ITEMS) {
            return false;
        }

        dashboardItemStore.save(item);
        dashboardElementStore.save(element);

        return true;
    }


    /**
     * Returns an item from this dashboard of the given type which number of
     * content is less than max. Returns null if no item matches the criteria.
     *
     * @param type the type of content to return.
     * @return an item.
     */
    @Override
    public DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        List<DashboardItem> items = dashboardItemStore
                .filter(dashboard, State.TO_DELETE);

        if (items == null || items.isEmpty()) {
            return null;
        }

        for (DashboardItem item : items) {
            if (type.equals(item.getType()) &&
                    dashboardItemService.getContentCount(item) < DashboardItem.MAX_CONTENT) {
                return item;
            }
        }

        return null;
    }

    int getDashboardItemCount(Dashboard dashboard) {
        List<DashboardItem> items = dashboardItemStore
                .filter(dashboard, State.TO_DELETE);
        return items == null ? 0 : items.size();
    }

    static boolean isItemContentTypeEmbedded(DashboardItemContent content) {
        switch (content.getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
            case DashboardItemContent.TYPE_EVENT_REPORT:
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                return true;
            }
            case DashboardItemContent.TYPE_USERS:
            case DashboardItemContent.TYPE_REPORTS:
            case DashboardItemContent.TYPE_RESOURCES: {
                return false;
            }
        }

        throw new IllegalArgumentException("Unsupported DashboardItemContent type");
    }
}
