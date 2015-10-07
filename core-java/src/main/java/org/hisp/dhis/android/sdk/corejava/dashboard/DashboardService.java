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

package org.hisp.dhis.android.sdk.corejava.dashboard;

import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardService implements IDashboardService {
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    private final IStateStore stateStore;

    public DashboardService(IDashboardStore dashboardStore,
                            IDashboardItemStore dashboardItemStore,
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
     * Factory method for creating DashboardElement.
     *
     * @param dashboardItem    DashboardItem to associate with element.
     * @param dashboardContent Content from which element will be created.
     * @return new element.
     * @throws IllegalArgumentException when dashboardItem or dashboardContent is null.
     */
    // @Override
    DashboardElement add(DashboardItem dashboardItem, DashboardContent dashboardContent) {
        isNull(dashboardItem, "dashboardItem must not be null");
        isNull(dashboardContent, "dashboardContent must not be null");

        DashboardElement element = new DashboardElement();
        element.setUId(dashboardContent.getUId());
        element.setName(dashboardContent.getName());
        element.setDisplayName(dashboardContent.getDisplayName());
        element.setCreated(dashboardContent.getCreated());
        element.setLastUpdated(dashboardContent.getLastUpdated());
        element.setDashboardItem(dashboardItem);

        // element.setAction(Action.TO_POST);
        stateStore.saveActionForModel(element, Action.TO_POST);

        return element;
    }


    /**
     * Factory method which creates and returns DashboardItem.
     *
     * @param dashboard Dashboard to associate with item.
     * @param content   Content for dashboard item.
     * @return new item.
     */
    // @Override
    boolean add(Dashboard dashboard, DashboardContent content) {
        isNull(dashboard, "dashboard must not be null");
        isNull(content, "content must not be null");

        /* DateTime lastUpdated = new DateTime();

        DashboardItem item = new DashboardItem();
        item.setCreated(lastUpdated);
        item.setLastUpdated(lastUpdated);
        item.setDashboard(dashboard);
        item.setAccess(Access.createDefaultAccess());
        item.setType(content.getType());

        stateStore.save(item, Action.TO_POST);

        return item; */
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(Dashboard dashboard) {
        dashboardStore.insert(dashboard);
        stateStore.saveActionForModel(dashboard, Action.TO_POST);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save(Dashboard object) {
        dashboardStore.save(object);

        // TODO check if dashboard was created earlier (then set correct flag)
        Action action = stateStore.queryActionForModel(object);

        if (action == null) {
            stateStore.saveActionForModel(object, Action.TO_POST);
        } else {
            stateStore.saveActionForModel(object, Action.TO_UPDATE);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Dashboard dashboard) {
        isNull(dashboard, "dashboard argument must not be null");

        Action action = stateStore.queryActionForModel(dashboard);
        if (Action.TO_DELETE.equals(action)) {
            throw new IllegalArgumentException("The name of dashboard with Action." +
                    "TO_DELETE cannot be updated");
        }

        /* if dashboard was not posted to the server before,
        you don't have anything to update */
        if (!Action.TO_POST.equals(action)) {
            // dashboard.setAction(Action.TO_UPDATE);
            stateStore.saveActionForModel(dashboard, Action.TO_UPDATE);
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
    public List<Dashboard> list() {
        return stateStore.filterModelsByAction(Dashboard.class, Action.TO_DELETE);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Dashboard dashboard) {
        isNull(dashboard, "dashboard argument must not be null");

        Action action = stateStore.queryActionForModel(dashboard);
        if (Action.TO_DELETE.equals(action)) {
            stateStore.deleteActionForModel(dashboard);
            dashboardStore.delete(dashboard);
        } else {
            stateStore.saveActionForModel(dashboard, Action.TO_DELETE);
            dashboardStore.update(dashboard);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dashboard get(long id) {
        Dashboard dashboard = dashboardStore.queryById(id);
        Action action = stateStore.queryActionForModel(dashboard);

        if (!Action.TO_DELETE.equals(action)) {
            return dashboard;
        }

        return null;
    }

    @Override
    public Dashboard get(String uid) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addContent(Dashboard dashboard, DashboardContent content) {
        /* isNull(dashboard, "Dashboard object must not be null");
        isNull(content, "DashboardContent object must not be null");

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

        return true; */
        return false;
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        isNull(dashboard, "dashboard must not be null");
        isNull(type, "type must not be null");

        List<DashboardItem> dashboardItems = queryRelateDashboardItems(dashboard);

        if (dashboardItems.isEmpty()) {
            return null;
        }

        /* for (DashboardItem item : dashboardItems) {
            if (type.equals(item.getType()) &&
                    dashboardItemService.getContentCount(item) < DashboardItem.MAX_CONTENT) {
                return item;
            }
        } */

        return null;
    }

    int getDashboardItemCount(Dashboard dashboard) {
        List<DashboardItem> dashboardItems = queryRelateDashboardItems(dashboard);
        return dashboardItems == null ? 0 : dashboardItems.size();
    }

    private List<DashboardItem> queryRelateDashboardItems(Dashboard dashboard) {
        List<DashboardItem> allDashboardItems = dashboardItemStore.queryByDashboard(dashboard);
        Map<Long, Action> actionMap = stateStore.queryActionsForModel(DashboardItem.class);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        for (DashboardItem dashboardItem : allDashboardItems) {
            Action action = actionMap.get(dashboardItem.getId());

            if (!Action.TO_DELETE.equals(action)) {
                dashboardItems.add(dashboardItem);
            }
        }

        return dashboardItems;
    }

    static boolean isItemContentTypeEmbedded(DashboardContent content) {
        switch (content.getType()) {
            case DashboardContent.TYPE_CHART:
            case DashboardContent.TYPE_EVENT_CHART:
            case DashboardContent.TYPE_MAP:
            case DashboardContent.TYPE_EVENT_REPORT:
            case DashboardContent.TYPE_REPORT_TABLE: {
                return true;
            }
            case DashboardContent.TYPE_USERS:
            case DashboardContent.TYPE_REPORTS:
            case DashboardContent.TYPE_RESOURCES: {
                return false;
            }
        }

        throw new IllegalArgumentException("Unsupported DashboardContent type: " +
                content.getType() + " name: " + content.getDisplayName());
    }
}
