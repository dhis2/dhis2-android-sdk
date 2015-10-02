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

import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardItemService implements IDashboardItemService {
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IStateStore stateStore;

    public DashboardItemService(IDashboardItemStore dashboardItemStore,
                                IDashboardElementStore dashboardElementStore,
                                IStateStore stateStore) {
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.stateStore = stateStore;
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
     * This method will change the action of the model to TO_DELETE
     * if the model was already synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from embedded database.
     */
    @Override
    public boolean remove(DashboardItem dashboardItem) {
        Action action = stateStore.queryAction(dashboardItem);
        if (Action.TO_POST.equals(action)) {
            stateStore.delete(dashboardItem);
            dashboardItemStore.delete(dashboardItem);
        } else {
            stateStore.save(dashboardItem, Action.TO_DELETE);
            dashboardItemStore.update(dashboardItem);
        }
        return true;
    }

    @Override
    public List<DashboardItem> list() {
        return stateStore.filterByAction(DashboardItem.class, Action.TO_DELETE);
    }

    @Override
    public List<DashboardItem> list(Dashboard dashboard) {
        List<DashboardItem> dashboardItems = dashboardItemStore.queryByDashboard(dashboard);
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

    /* @Override
    public List<DashboardItem> filterByType(Dashboard dashboard, String type) {
        // List<DashboardItem> dashboardItems = dashboardItemStore.filterByType(dashboard, type);
        List<DashboardItem> dashboardItems = new ArrayList<>();
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
    } */

    @Override
    public DashboardItem get(long id) {
        DashboardItem dashboardItem = dashboardItemStore.queryById(id);

        if (dashboardItem != null) {
            Action action = stateStore.queryAction(dashboardItem);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboardItem;
            }
        }

        return null;
    }

    @Override
    public DashboardItem get(String uid) {
        DashboardItem dashboardItem = dashboardItemStore.queryByUid(uid);

        if (dashboardItem != null) {
            Action action = stateStore.queryAction(dashboardItem);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboardItem;
            }
        }

        return null;
    }

    //@Override
    int getContentCount(DashboardItem dashboardItem) {
        List<DashboardElement> dashboardElements = queryRelatedElements(dashboardItem);
        return dashboardElements == null ? 0 : dashboardElements.size();
    }

    private List<DashboardElement> queryRelatedElements(DashboardItem dashboardItem) {
        List<DashboardElement> allDashboardElements = dashboardElementStore.queryByDashboardItem(dashboardItem);
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardElement.class);

        List<DashboardElement> dashboardElements = new ArrayList<>();
        if (allDashboardElements != null && !allDashboardElements.isEmpty()) {
            for (DashboardElement dashboardElement : dashboardElements) {
                Action action = actionMap.get(dashboardElement.getId());

                if (!Action.TO_DELETE.equals(action)) {
                    dashboardElements.add(dashboardElement);
                }
            }
        }

        return dashboardElements;
    }
}
