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
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class DashboardServiceImpl implements DashboardService {
    private final DashboardStore dashboardStore;
    private final DashboardItemStore dashboardItemStore;
    private final DashboardElementStore dashboardElementStore;
    private final StateStore stateStore;
    private final DashboardItemService dashboardItemService;
    private final DashboardElementService dashboardElementService;

    public DashboardServiceImpl(DashboardStore dashboardStore, DashboardItemStore dashboardItemStore,
                                DashboardElementStore dashboardElementStore, StateStore stateStore,
                                DashboardItemService dashboardItemService,
                                DashboardElementService dashboardElementService) {
        this.dashboardStore = dashboardStore;
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.stateStore = stateStore;
        this.dashboardItemService = dashboardItemService;
        this.dashboardElementService = dashboardElementService;
    }

    private static boolean isItemContentTypeEmbedded(String type) {
        if (type != null) {
            switch (type) {
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
        }

        throw new IllegalArgumentException("Unsupported DashboardContent type: " + type);
    }

    @Override
    public Dashboard create(String name) {
        isNull(name, "Name must not be null");

        DateTime dateTime = DateTime.now();
        Access access = Access.createDefaultAccess();

        Dashboard dashboard = new Dashboard();
        dashboard.setUId(CodeGenerator.generateCode());
        dashboard.setCreated(dateTime);
        dashboard.setLastUpdated(dateTime);
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setAccess(access);

        return dashboard;
    }

    @Override
    public boolean save(Dashboard object) {
        isNull(object, "Dashboard object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = dashboardStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = dashboardStore.save(object);
                break;
            }
            case SYNCED: {
                status = dashboardStore.save(object);

                if (status) {
                    status = stateStore.saveActionForModel(object, Action.TO_UPDATE);
                }
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }

        }

        return status;
    }

    @Override
    public boolean remove(Dashboard object) {
        isNull(object, "Dashboard object must not be null");

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
                status = dashboardStore.delete(object);
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }
        }

        return status;
    }

    @Override
    public Dashboard get(long id) {
        Dashboard dashboard = dashboardStore.queryById(id);

        if (dashboard != null) {
            Action action = stateStore.queryActionForModel(dashboard);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboard;
            }
        }
        return null;
    }

    @Override
    public Dashboard get(String uid) {
        Dashboard dashboard = dashboardStore.queryByUid(uid);

        if (dashboard != null) {
            Action action = stateStore.queryActionForModel(dashboard);

            if (!Action.TO_DELETE.equals(action)) {
                return dashboard;
            }
        }
        return null;
    }

    @Override
    public List<Dashboard> list() {
        return stateStore.queryModelsWithActions(Dashboard.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public List<Dashboard> listByActions(Set<Action> actionSet) {
        isNull(actionSet, "actionSet must not be null");
        if (actionSet.isEmpty()) {
            throw new IllegalArgumentException("You must provide atleast one action to list by.");
        }
        return stateStore.queryModelsWithActions(Dashboard.class, actionSet.toArray(new Action[actionSet.size()]));
    }

    @Override
    public int countItems(Dashboard dashboard) {
        isNull(dashboard, "Dashboard object must not be null");

        List<DashboardItem> dashboardItems = dashboardItemService.list(dashboard);
        return dashboardItems != null ? dashboardItems.size() : 0;
    }

    @Override
    public boolean addContent(Dashboard dashboard, DashboardContent content) {
        isNull(dashboard, "Dashboard object must not be null");
        isNull(content, "DashboardContent object must not be null");

        DashboardItem item;
        DashboardElement element;
        int itemsCount = countItems(dashboard);

        if (isItemContentTypeEmbedded(content.getType())) {
            item = dashboardItemService.create(dashboard, content.getType());
            element = dashboardElementService.create(item, content);
            itemsCount += 1;
        } else {
            item = getAvailableItemByType(dashboard, content.getType());
            if (item == null) {
                item = dashboardItemService.create(dashboard, content.getType());
                itemsCount += 1;
            }
            element = dashboardElementService.create(item, content);
        }

        if (itemsCount > Dashboard.MAX_ITEMS) {
            return false;
        }

        return dashboardItemStore.save(item) && dashboardElementStore.save(element);
    }

    private DashboardItem getAvailableItemByType(Dashboard dashboard, String type) {
        List<DashboardItem> dashboardItems = dashboardItemService.list(dashboard);
        for (DashboardItem item : dashboardItems) {
            if (type.equals(item.getType()) && dashboardItemService.countElements(item) <
                    DashboardItem.MAX_CONTENT) {
                return item;
            }
        }

        return null;
    }
}
