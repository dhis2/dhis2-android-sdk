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

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardService implements IDashboardService {
    private final IIdentifiableObjectStore<Dashboard> dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    private final IStateStore stateStore;

    public DashboardService(IIdentifiableObjectStore<Dashboard> dashboardStore, IDashboardItemStore dashboardItemStore,
                            IDashboardElementStore dashboardElementStore,
                            IDashboardItemService dashboardItemService,
                            IDashboardElementService dashboardElementService,
                            IStateStore stateStore) {
        this.dashboardStore = dashboardStore;
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemService = dashboardItemService;
        this.dashboardElementService = dashboardElementService;
        this.stateStore = stateStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Dashboard dashboard) {
        /* DateTime lastUpdated = new DateTime();

        Dashboard dashboard = new Dashboard();
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(lastUpdated);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setAccess(Access.createDefaultAccess());
        // dashboard.setAction(Action.TO_POST);
        return dashboard; */

        dashboardStore.insert(dashboard);
        stateStore.save(dashboard, Action.TO_POST);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save(Dashboard object) {
        dashboardStore.save(object);

        // TODO check if dashboard was created earlier (then set correct flag)
        Action action = stateStore.queryAction(object);

        if (action == null) {
            stateStore.save(object, Action.TO_POST);
        } else {
            stateStore.save(object, Action.TO_UPDATE);
        }

        return true;
    }

    @Override
    public List<Dashboard> query() {
        return stateStore.filterByAction(Dashboard.class, Action.TO_DELETE, "DashboardService");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Dashboard dashboard) {
        isNull(dashboard, "dashboard argument must not be null");

        Action action = stateStore.queryAction(dashboard);
        if (Action.TO_DELETE.equals(action)) {
            // dashboard.setAction(Action.TO_DELETE);
            stateStore.delete(dashboard);
            dashboardStore.delete(dashboard);
        } else {
            // dashboard.setAction(Action.TO_DELETE);
            stateStore.save(dashboard, Action.TO_DELETE);
            dashboardStore.update(dashboard);
        }

        return true;
    }

    @Override
    public Dashboard query(long id) {
        Dashboard dashboard = dashboardStore.query(id);
        Action action = stateStore.queryAction(dashboard);

        if (!Action.TO_DELETE.equals(action)) {
            return dashboard;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Dashboard dashboard) {
        isNull(dashboard, "dashboard argument must not be null");

        Action action = stateStore.queryAction(dashboard);
        if (Action.TO_DELETE.equals(action)) {
            throw new IllegalArgumentException("The name of dashboard with Action." +
                    "TO_DELETE cannot be updated");
        }

        /* if dashboard was not posted to the server before,
        you don't have anything to update */
        if (!Action.TO_POST.equals(action)) {
            // dashboard.setAction(Action.TO_UPDATE);
            stateStore.save(dashboard, Action.TO_UPDATE);
        }

        /* dashboard.setName(name);
        dashboard.setDisplayName(name); */

        dashboardStore.update(dashboard);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content) {
        isNull(dashboard, "Dashboard object must not be null");
        isNull(content, "DashboardItemContent object must not be null");

        DashboardItem item;
        DashboardElement element;
        int itemsCount = getDashboardItemCount(dashboard);

        if (isItemContentTypeEmbedded(content)) {
            item = dashboardItemService.add(dashboard, content);
            element = dashboardElementService.add(item, content);
            itemsCount += 1;
        } else {
            item = getAvailableItemByType(dashboard, content.getType());
            if (item == null) {
                item = dashboardItemService.add(dashboard, content);
                itemsCount += 1;
            }
            element = dashboardElementService.add(item, content);
        }

        if (itemsCount > Dashboard.MAX_ITEMS) {
            return false;
        }

        dashboardItemStore.save(item);
        dashboardElementStore.save(element);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        isNull(dashboard, "dashboard must not be null");
        isNull(type, "type must not be null");

        List<DashboardItem> dashboardItems = queryRelateDashboardItems(dashboard);

        if (dashboardItems.isEmpty()) {
            return null;
        }

        for (DashboardItem item : dashboardItems) {
            if (type.equals(item.getType()) &&
                    dashboardItemService.getContentCount(item) < DashboardItem.MAX_CONTENT) {
                return item;
            }
        }

        return null;
    }

    int getDashboardItemCount(Dashboard dashboard) {
        List<DashboardItem> dashboardItems = queryRelateDashboardItems(dashboard);
        return dashboardItems == null ? 0 : dashboardItems.size();
    }

    private List<DashboardItem> queryRelateDashboardItems(Dashboard dashboard) {
        List<DashboardItem> allDashboardItems = dashboardItemStore.query(dashboard);
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardItem.class);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        for (DashboardItem dashboardItem : allDashboardItems) {
            Action action = actionMap.get(dashboardItem.getId());

            if (!Action.TO_DELETE.equals(action)) {
                dashboardItems.add(dashboardItem);
            }
        }

        return dashboardItems;
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

        throw new IllegalArgumentException("Unsupported DashboardItemContent type: " +
                content.getType() + " name: " + content.getDisplayName());
    }
}
