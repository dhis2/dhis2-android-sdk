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
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DashboardItem> list() {
        return stateStore.filterByAction(DashboardItem.class, Action.TO_DELETE);
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
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
}
