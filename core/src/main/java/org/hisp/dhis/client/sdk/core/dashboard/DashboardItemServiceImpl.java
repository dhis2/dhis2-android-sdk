/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.dashboard;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.utils.Preconditions;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardItemServiceImpl implements DashboardItemService {
    private final DashboardItemStore dashboardItemStore;
    private final StateStore stateStore;
    private final DashboardElementService dashboardElementService;

    public DashboardItemServiceImpl(DashboardItemStore dashboardItemStore, StateStore stateStore,
                                    DashboardElementService dashboardElementService) {
        this.dashboardItemStore = dashboardItemStore;
        this.stateStore = stateStore;
        this.dashboardElementService = dashboardElementService;
    }

    @Override
    public DashboardItem create(Dashboard dashboard, String type) {
        Preconditions.isNull(dashboard, "Dashboard object must not be null");
        Preconditions.isNull(type, "Type must not be null");

        switch (type) {
            case DashboardContent.TYPE_CHART:
            case DashboardContent.TYPE_EVENT_CHART:
            case DashboardContent.TYPE_MAP:
            case DashboardContent.TYPE_REPORT_TABLE:
            case DashboardContent.TYPE_USERS:
            case DashboardContent.TYPE_REPORTS:
            case DashboardContent.TYPE_EVENT_REPORT:
            case DashboardContent.TYPE_RESOURCES:
            case DashboardContent.TYPE_MESSAGES:
                break;
            default:
                throw new IllegalArgumentException("Unsupported DashboardContent type: " + type);
        }

        String uid = CodeGenerator.generateCode();
        DateTime created = DateTime.now();
        Access access = Access.createDefaultAccess();

        DashboardItem dashboardItem = new DashboardItem();
        dashboardItem.setUId(uid);
        dashboardItem.setCreated(created);
        dashboardItem.setLastUpdated(created);
        dashboardItem.setName(uid);
        dashboardItem.setDisplayName(uid);
        dashboardItem.setAccess(access);

        dashboardItem.setType(type);
        dashboardItem.setShape(DashboardItem.SHAPE_NORMAL);
        dashboardItem.setDashboard(dashboard);

        return dashboardItem;
    }

    @Override
    public int countElements(DashboardItem dashboardItem) {
        Preconditions.isNull(dashboardItem, "DashboardItem object must not be null");

        List<DashboardElement> dashboardElements = dashboardElementService.list(dashboardItem);
        return dashboardElements != null ? dashboardElements.size() : 0;
    }

    @Override
    public List<DashboardItem> list(String uId) {
        Preconditions.isNull(uId, "id must not be null");

        List<DashboardItem> allDashboardItems = dashboardItemStore.query(uId);
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

    @Override
    public List<DashboardItem> list(Dashboard dashboard) {
        Preconditions.isNull(dashboard, "Dashboard object must not be null");

        List<DashboardItem> allDashboardItems = dashboardItemStore.query(dashboard);
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

    @Override
    public List<DashboardItem> list() {
        return stateStore.queryModelsWithActions(DashboardItem.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public DashboardItem get(long id) {
        DashboardItem dashboardItem = dashboardItemStore.queryById(id);

        if (dashboardItem != null) {
            Action action = stateStore.queryActionForModel(dashboardItem);

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
            Action action = stateStore.queryActionForModel(dashboardItem);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboardItem;
            }
        }

        return null;
    }

    @Override
    public boolean remove(DashboardItem object) {
        Preconditions.isNull(object, "DashboardItem object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            return false;
        }

        boolean status = false;
        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                status = stateStore.saveActionForModel(object, Action.TO_DELETE);
                break;
            }
            case TO_POST: {
                status = dashboardItemStore.delete(object);
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }
        }

        return status;
    }
}
