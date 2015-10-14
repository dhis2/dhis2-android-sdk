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

package org.hisp.dhis.android.sdk.core.controllers;

import android.net.Uri;

import org.hisp.dhis.android.sdk.core.controllers.common.IDataController;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.api.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.models.ResourceType;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.core.api.utils.DbUtils;
import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import retrofit.client.Header;
import retrofit.client.Response;

import static org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils.findLocationHeader;
import static org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils.handleApiException;
import static org.hisp.dhis.android.sdk.core.api.utils.NetworkUtils.unwrapResponse;
import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.merge;
import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.toListIds;
import static org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject.toMap;

public final class DashboardController implements IDataController<Dashboard> {
    private final IDhisApi dhisApi;
    private final IIdentifiableObjectStore<Dashboard> dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;

    private final IStateStore stateStore;

    public DashboardController(IDhisApi dhisApi, IIdentifiableObjectStore<Dashboard> dashboardStore,
                               IDashboardItemStore dashboardItemStore,
                               IDashboardElementStore dashboardElementStore,
                               IDashboardItemContentStore dashboardItemContentStore,
                               IStateStore stateStore) {
        this.dhisApi = dhisApi;
        this.dashboardStore = dashboardStore;
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemContentStore = dashboardItemContentStore;
        this.stateStore = stateStore;
    }

    /* this method subtracts content of bList from aList */
    private static List<String> subtract(List<String> aList, List<String> bList) {
        List<String> aListCopy = new ArrayList<>(aList);
        if (bList != null && !bList.isEmpty()) {
            for (String bItem : bList) {
                if (aListCopy.contains(bItem)) {
                    int index = aListCopy.indexOf(bItem);
                    aListCopy.remove(index);
                }
            }
        }
        return aListCopy;
    }

    private void getDashboardDataFromServer() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();

        List<Dashboard> dashboards = updateDashboards(lastUpdated);
        List<DashboardItem> dashboardItems = updateDashboardItems(dashboards, lastUpdated);

        Queue<IDbOperation> operations = new LinkedList<>();
        /* operations.addAll(DbUtils.createOperations(dashboardStore,
                dashboardStore.filter(Action.TO_POST), dashboards)); */
        operations.addAll(DbUtils.createOperations(dashboardStore,
                stateStore.filterModelsByAction(Dashboard.class, Action.TO_POST), dashboards));
        /* operations.addAll(DbUtils.createOperations(dashboardItemStore,
                dashboardItemStore.filter(Action.TO_POST), dashboardItems)); */
        operations.addAll(DbUtils.createOperations(dashboardItemStore,
                stateStore.filterModelsByAction(DashboardItem.class, Action.TO_POST), dashboardItems));
        operations.addAll(createOperations(dashboardItems));

        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS, serverDateTime);
    }

    private List<Dashboard> updateDashboards(DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access";

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", BASE + ",dashboardItems" +
                "[" + BASE + ",type,shape,messages," +
                "chart" + "[" + BASE + "]," +
                "eventChart" + "[" + BASE + "]" +
                "map" + "[" + BASE + "]," +
                "reportTable" + "[" + BASE + "]," +
                "eventReport" + "[" + BASE + "]," +
                "users" + "[" + BASE + "]," +
                "reports" + "[" + BASE + "]," +
                "resources" + "[" + BASE + "]" +
                "]");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        // List of dashboards with UUIDs (without content). This list is used
        // only to determine what was removed on server.
        List<Dashboard> actualDashboards = unwrapResponse(dhisApi
                .getDashboards(QUERY_MAP_BASIC), "dashboards");

        // List of updated dashboards with content.
        List<Dashboard> updatedDashboards = unwrapResponse(dhisApi
                .getDashboards(QUERY_MAP_FULL), "dashboards");

        // Building dashboard item to dashboard relationship.
        if (updatedDashboards != null && !updatedDashboards.isEmpty()) {
            for (Dashboard dashboard : updatedDashboards) {
                if (dashboard == null || dashboard.getDashboardItems().isEmpty()) {
                    continue;
                }

                for (DashboardItem item : dashboard.getDashboardItems()) {
                    item.setDashboard(dashboard);
                }
            }
        }

        // List of persisted dashboards.
        // List<Dashboard> persistedDashboards = dashboardStore.filter(Action.TO_POST);
        List<Dashboard> persistedDashboards = stateStore.filterModelsByAction(Dashboard.class, Action.TO_POST);
        if (persistedDashboards != null && !persistedDashboards.isEmpty()) {
            Map<Long, List<DashboardItem>> dashboardItemMap = getDashboardItemMap();
            Map<Long, List<DashboardElement>> dashboardElementMap = getDashboardElementMap(false);

            for (Dashboard dashboard : persistedDashboards) {
                /* List<DashboardItem> items = dashboardItemStore
                        .filter(dashboard, Action.TO_POST); */

                // List<DashboardItem> items = filterDashboardItems(dashboard, Action.TO_POST);
                List<DashboardItem> items = dashboardItemMap.get(dashboard.getId());
                if (items == null || items.isEmpty()) {
                    continue;
                }

                for (DashboardItem item : items) {
                    /* List<DashboardElement> dashboardElements =
                            dashboardElementStore.filter(item, Action.TO_POST); */
                    // item.setDashboardElements(dashboardElements);

                    /* List<DashboardElement> dashboardElements =
                            filterDashboardElements(item, Action.TO_POST); */
                    List<DashboardElement> dashboardElements =
                            dashboardElementMap.get(item.getId());
                    item.setDashboardElements(dashboardElements);
                }
                dashboard.setDashboardItems(items);
            }
        }

        return merge(actualDashboards, updatedDashboards, persistedDashboards);
    }

    private Map<Long, List<DashboardItem>> getDashboardItemMap() {
        List<DashboardItem> dashboardItemsList = stateStore
                .filterModelsByAction(DashboardItem.class, Action.TO_POST);
        Map<Long, List<DashboardItem>> dashboardItemMap = new HashMap<>();

        for (DashboardItem dashboardItem : dashboardItemsList) {
            Long dashboardId = dashboardItem.getDashboard().getId();

            List<DashboardItem> bag = dashboardItemMap.get(dashboardId);
            if (bag == null) {
                bag = new ArrayList<>();
                dashboardItemMap.put(dashboardId, bag);
            }

            bag.add(dashboardItem);
        }

        return dashboardItemMap;
    }

    private Map<Long, List<DashboardElement>> getDashboardElementMap(boolean withAction) {
        List<DashboardElement> dashboardElementsList;

        if (withAction) {
            dashboardElementsList = stateStore.queryModelsWithAction(
                    DashboardElement.class, Action.TO_POST);
        } else {
            dashboardElementsList = stateStore.filterModelsByAction(
                    DashboardElement.class, Action.TO_POST);
        }
        Map<Long, List<DashboardElement>> dashboardElementMap = new HashMap<>();

        for (DashboardElement dashboardElement : dashboardElementsList) {
            Long dashboardItemId = dashboardElement.getDashboardItem().getId();

            List<DashboardElement> bag = dashboardElementMap.get(dashboardItemId);
            if (bag == null) {
                bag = new ArrayList<>();
                dashboardElementMap.put(dashboardItemId, bag);
            }

            bag.add(dashboardElement);
        }

        return dashboardElementMap;
    }


    /* private List<DashboardItem> filterDashboardItems(Dashboard dashboard, Action action) {
        List<DashboardItem> filteredItems = new ArrayList<>();
        List<DashboardItem> dbItems = stateStore.filterByAction(DashboardItem.class, action);

        if (dbItems != null && !dbItems.isEmpty()) {
            for (DashboardItem dashboardItem : dbItems) {
                if (dashboard.getId() == dashboardItem.getDashboard().getId()) {
                    filteredItems.add(dashboardItem);
                }
            }
        }

        return filteredItems;
    }

    private List<DashboardElement> filterDashboardElements(DashboardItem item, Action action) {
        List<DashboardElement> filteredElements = new ArrayList<>();
        List<DashboardElement> dbElements = stateStore.filterByAction(DashboardElement.class, action);

        if (dbElements != null && !dbElements.isEmpty()) {
            for (DashboardElement dbElement : dbElements) {
                if (item.getId() == dbElement.getDashboardItem().getId()) {
                    filteredElements.add(dbElement);
                }
            }
        }

        return filteredElements;
    }

    private List<DashboardElement> getDashboardElements(DashboardItem item, Action action) {
        List<DashboardElement> filteredElements = new ArrayList<>();
        List<DashboardElement> dbElements = stateStore.queryWithAction(DashboardElement.class, action);

        if (dbElements != null && !dbElements.isEmpty()) {
            for (DashboardElement dbElement : dbElements) {
                if (item.getId() == dbElement.getDashboardItem().getId()) {
                    filteredElements.add(dbElement);
                }
            }
        }

        return filteredElements;
    } */

    private List<DashboardItem> updateDashboardItems(List<Dashboard> dashboards, DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id,created,lastUpdated,shape");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        // List of actual dashboard items.
        List<DashboardItem> actualItems = new ArrayList<>();
        if (dashboards != null && !dashboards.isEmpty()) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard.getDashboardItems() != null) {
                    actualItems.addAll(dashboard.getDashboardItems());
                }
            }
        }

        // List of persisted dashboard items
        /* Map<String, DashboardItem> persistedDashboardItems =
                toMap(dashboardItemStore.filter(Action.TO_POST)); */
        Map<String, DashboardItem> persistedDashboardItems =
                toMap(stateStore.filterModelsByAction(DashboardItem.class, Action.TO_POST));

        // List of updated dashboard items. We need this only to get
        // information about updates of item shape.
        List<DashboardItem> updatedItems = unwrapResponse(dhisApi
                .getDashboardItems(QUERY_MAP_BASIC), "dashboardItems");
        // Map of items where keys are UUIDs.
        Map<String, DashboardItem> updatedItemsMap = toMap(updatedItems);

        // merging updated items with actual
        for (DashboardItem actualItem : actualItems) {
            DashboardItem updatedItem = updatedItemsMap.get(actualItem.getUId());
            DashboardItem persistedItem = persistedDashboardItems.get(actualItem.getUId());

            if (persistedItem != null) {
                actualItem.setId(persistedItem.getId());
            }

            if (updatedItem != null) {
                actualItem.setCreated(updatedItem.getCreated());
                actualItem.setLastUpdated(updatedItem.getLastUpdated());
                actualItem.setShape(updatedItem.getShape());
            }

            if (actualItem.getDashboardElements() == null ||
                    actualItem.getDashboardElements().isEmpty()) {
                continue;
            }

            // building dashboard element to item relationship.
            for (DashboardElement element : actualItem.getDashboardElements()) {
                element.setDashboardItem(actualItem);
            }
        }

        return actualItems;
    }

    private List<DbOperation> createOperations(List<DashboardItem> refreshedItems) {
        List<DbOperation> dbOperations = new ArrayList<>();

        Map<Long, List<DashboardElement>> dashboardElementMap = getDashboardElementMap(false);
        for (DashboardItem refreshedItem : refreshedItems) {
            /* List<DashboardElement> persistedElementList =
                    dashboardElementStore.filter(refreshedItem, Action.TO_POST); */
            /* List<DashboardElement> persistedElementList =
                    filterDashboardElements(refreshedItem, Action.TO_POST); */
            List<DashboardElement> persistedElementList =
                    dashboardElementMap.get(refreshedItem.getId());

            List<DashboardElement> refreshedElementList =
                    refreshedItem.getDashboardElements();

            if (persistedElementList == null) {
                persistedElementList = new ArrayList<>();
            }

            if (refreshedElementList == null) {
                refreshedElementList = new ArrayList<>();
            }

            List<String> persistedElementIds = toListIds(persistedElementList);
            List<String> refreshedElementIds = toListIds(refreshedElementList);

            List<String> itemIdsToInsert = subtract(refreshedElementIds, persistedElementIds);
            List<String> itemIdsToDelete = subtract(persistedElementIds, refreshedElementIds);

            for (String elementToDelete : itemIdsToDelete) {
                int index = persistedElementIds.indexOf(elementToDelete);
                DashboardElement element = persistedElementList.get(index);
                dbOperations.add(DbOperation
                        .with(dashboardElementStore)
                        .delete(element));

                persistedElementIds.remove(index);
                persistedElementList.remove(index);
            }

            for (String elementToInsert : itemIdsToInsert) {
                int index = refreshedElementIds.indexOf(elementToInsert);
                DashboardElement dashboardElement = refreshedElementList.get(index);
                dbOperations.add(DbOperation
                        .with(dashboardElementStore)
                        .insert(dashboardElement));

                refreshedElementIds.remove(index);
                refreshedElementList.remove(index);
            }
        }

        return dbOperations;
    }


    private void sendLocalChanges() throws APIException {
        sendDashboardChanges();
        sendDashboardItemChanges();
        sendDashboardElements();
    }

    private void sendDashboardChanges() throws APIException {
        // we need to sort dashboards in natural order.
        // In order they were inserted in local database.

        // List<Dashboard> dashboards = dashboardStore.filter(Action.SYNCED);
        List<Dashboard> dashboards = stateStore
                .filterModelsByAction(Dashboard.class, Action.SYNCED);
        Map<Long, Action> actionMap = stateStore
                .queryActionsForModel(Dashboard.class);
        if (dashboards == null || dashboards.isEmpty()) {
            return;
        }

        for (Dashboard dashboard : dashboards) {
            Action action = actionMap.get(dashboard.getId());
            action = action == null ? Action.SYNCED : action;

            switch (action) {
                case TO_POST: {
                    postDashboard(dashboard);
                    break;
                }
                case TO_UPDATE: {
                    putDashboard(dashboard);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboard(dashboard);
                    break;
                }
            }
        }
    }

    private void postDashboard(Dashboard dashboard) throws APIException {
        try {
            Response response = dhisApi.postDashboard(dashboard);
            // also, we will need to find UUID of newly created dashboard,
            // which is contained inside of HTTP Location header
            Header header = findLocationHeader(response.getHeaders());
            // parse the value of header as URI and extract the id
            String dashboardId = Uri.parse(header.getValue()).getLastPathSegment();
            // set UUID, change state and save dashboard
            dashboard.setUId(dashboardId);
            // dashboard.setAction(Action.SYNCED);

            dashboardStore.save(dashboard);
            stateStore.saveActionForModel(dashboard, Action.SYNCED);

            updateDashboardTimeStamp(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException);
        }
    }

    private void putDashboard(Dashboard dashboard) throws APIException {
        try {
            dhisApi.putDashboard(dashboard.getUId(), dashboard);

            // dashboard.setAction(Action.SYNCED);
            dashboardStore.save(dashboard);
            stateStore.saveActionForModel(dashboard, Action.SYNCED);

            updateDashboardTimeStamp(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException, dashboard, dashboardStore);
        }
    }

    private void deleteDashboard(Dashboard dashboard) throws APIException {
        try {
            dhisApi.deleteDashboard(dashboard.getUId());

            dashboardStore.delete(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException, dashboard, dashboardStore);
        }
    }

    private void sendDashboardItemChanges() throws APIException {
        /* List<DashboardItem> dashboardItems =
                dashboardItemStore.filter(Action.SYNCED); */
        List<DashboardItem> dashboardItems =
                stateStore.filterModelsByAction(DashboardItem.class, Action.SYNCED);
        Map<Long, Action> actionMap = stateStore.queryActionsForModel(DashboardItem.class);

        if (dashboardItems == null || dashboardItems.isEmpty()) {
            return;
        }

        for (DashboardItem dashboardItem : dashboardItems) {
            Action action = actionMap.get(dashboardItem.getId());
            if (action == null) {
                action = Action.SYNCED;
            }

            switch (action) {
                case TO_POST: {
                    postDashboardItem(dashboardItem);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboardItem(dashboardItem);
                    break;
                }
            }
        }
    }

    private void postDashboardItem(DashboardItem dashboardItem) throws APIException {
        Dashboard dashboard = dashboardItem.getDashboard();
        Action dashboardAction = stateStore.queryActionForModel(dashboard);

        if (dashboard != null && dashboardAction != null) {
            boolean isDashboardSynced = (dashboardAction.equals(Action.SYNCED) ||
                    dashboardAction.equals(Action.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            /* List<DashboardElement> elements =
                    dashboardElementStore.queryById(dashboardItem, Action.TO_POST); */
            /* List<DashboardElement> elements =
                    getDashboardElements(dashboardItem, Action.TO_POST); */
            List<DashboardElement> elements = getDashboardElementMap(true)
                    .get(dashboardItem.getId());

            if (elements == null || elements.isEmpty()) {
                return;
            }

            try {
                DashboardElement element = elements.get(0);
                Response response = dhisApi.postDashboardItem(dashboard.getUId(),
                        dashboardItem.getType(), element.getUId(), "");

                Header locationHeader = findLocationHeader(response.getHeaders());
                String dashboardItemUId = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment();
                dashboardItem.setUId(dashboardItemUId);
                // dashboardItem.setAction(Action.SYNCED);

                // element.setAction(Action.SYNCED);

                dashboardItemStore.save(dashboardItem);
                dashboardElementStore.save(element);

                stateStore.saveActionForModel(dashboardItem, Action.SYNCED);
                stateStore.saveActionForModel(dashboard, Action.SYNCED);

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, dashboardItem, dashboardItemStore);
            }
        }
    }

    private void deleteDashboardItem(DashboardItem dashboardItem) throws APIException {
        Dashboard dashboard = dashboardItem.getDashboard();
        Action dashboardAction = stateStore.queryActionForModel(dashboard);

        if (dashboard != null && dashboardAction != null) {
            boolean isDashboardSynced = (dashboardAction.equals(Action.SYNCED) ||
                    dashboardAction.equals(Action.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            try {
                dhisApi.deleteDashboardItem(dashboard.getUId(),
                        dashboardItem.getUId());
                dashboardItemStore.delete(dashboardItem);

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, dashboardItem, dashboardItemStore);
            }
        }
    }

    private void sendDashboardElements() throws APIException {
        /* List<DashboardElement> elements = new Select()
                .from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .STATE).isNot(Action.SYNCED))
                .orderBy(true, DashboardElement$Table.ID)
                .queryList(); */
        List<DashboardElement> elements = stateStore
                .filterModelsByAction(DashboardElement.class, Action.SYNCED);
        Map<Long, Action> actionMap = stateStore.queryActionsForModel(DashboardElement.class);

        if (elements == null || elements.isEmpty()) {
            return;
        }

        for (DashboardElement element : elements) {
            Action action = actionMap.get(element.getId());
            if (action == null) {
                action = Action.SYNCED;
            }

            switch (action) {
                case TO_POST: {
                    postDashboardElement(element);
                    break;
                }
                case TO_DELETE: {
                    deleteDashboardElement(element);
                    break;
                }
            }
        }
    }

    private void postDashboardElement(DashboardElement element) throws APIException {
        DashboardItem item = element.getDashboardItem();
        /* if (item == null || item.getAction() == null) {
            return;
        }

        Dashboard dashboard = item.getDashboard();
        if (dashboard == null || dashboard.getAction() == null) {
            return;
        }

        // we need to make sure that associated DashboardItem
        // and parent Dashboard are already synced to the server.
        boolean isDashboardSynced = (dashboard.getAction().equals(Action.SYNCED) ||
                dashboard.getAction().equals(Action.TO_UPDATE));
        boolean isItemSynced = item.getAction().equals(Action.SYNCED) ||
                item.getAction().equals(Action.TO_UPDATE);
        if (isDashboardSynced && isItemSynced) {

            try {
                dhisApi.postDashboardItem(dashboard.getUId(),
                        item.getType(), element.getUId(), "");
                element.setAction(Action.SYNCED);
                dashboardElementStore.save(element);

                updateDashboardTimeStamp(item.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, element, dashboardElementStore);
            }
        } */
    }

    private void deleteDashboardElement(DashboardElement element) throws APIException {
        DashboardItem item = element.getDashboardItem();
        Action itemAction = stateStore.queryActionForModel(item);

        if (item == null || itemAction == null) {
            return;
        }

        Dashboard dashboard = item.getDashboard();
        Action dashboardAction = stateStore.queryActionForModel(dashboard);
        if (dashboard == null || dashboardAction == null) {
            return;
        }

        // we need to make sure associated DashboardItem
        // and parent Dashboard are already synced to server
        boolean isDashboardSynced = (dashboardAction.equals(Action.SYNCED) ||
                dashboardAction.equals(Action.TO_UPDATE));
        boolean isItemSynced = dashboardAction.equals(Action.SYNCED) ||
                dashboardAction.equals(Action.TO_UPDATE);
        if (isDashboardSynced && isItemSynced) {
            try {
                dhisApi.deleteDashboardItemContent(dashboard.getUId(),
                        item.getUId(), element.getUId());

                dashboardElementStore.delete(element);

                // removal of elements changes
                // dashboard's timestamp on server. In order to stay in sync,
                // we need to get dashboard from server.
                updateDashboardTimeStamp(item.getDashboard());
            } catch (APIException apiException) {
                handleApiException(apiException, element, dashboardElementStore);
            }
        }
    }

    private void updateDashboardTimeStamp(Dashboard dashboard) throws APIException {
        try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "created,lastUpdated");
            Dashboard updatedDashboard = dhisApi
                    .getDashboard(dashboard.getUId(), QUERY_PARAMS);

            // merging updated timestamp to local dashboard model
            dashboard.setCreated(updatedDashboard.getCreated());
            dashboard.setLastUpdated(updatedDashboard.getLastUpdated());

            dashboardStore.save(dashboard);
        } catch (APIException apiException) {
            handleApiException(apiException);
        }
    }

    @Override
    public void sync() throws APIException {
        /* first we need to fetch all changes from server
        and apply them to local database */
        getDashboardDataFromServer();

        /* now we can try to send changes made locally to server */
        sendLocalChanges();

        /* sync content */
        syncDashboardContent();
    }

    public void syncDashboardContent() throws APIException {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS_CONTENT);
        DateTime serverDateTime = dhisApi
                .getSystemInfo().getServerDate();

        /* first we need to update api resources, dashboards
        and dashboard items */
        List<DashboardContent> dashboardContent =
                updateApiResources(lastUpdated);
        Queue<IDbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(dashboardItemContentStore,
                dashboardItemContentStore.queryAll(), dashboardContent));
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS_CONTENT, serverDateTime);
    }

    private List<DashboardContent> updateApiResources(DateTime lastUpdated) throws APIException {
        List<DashboardContent> dashboardContent = new ArrayList<>();
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_CHART, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_EVENT_CHART, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_MAP, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_REPORT_TABLE, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_EVENT_REPORT, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_USERS, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_REPORTS, lastUpdated));
        dashboardContent.addAll(updateApiResourceByType(
                DashboardContent.TYPE_RESOURCES, lastUpdated));
        return dashboardContent;
    }

    private List<DashboardContent> updateApiResourceByType(final String type,
                                                               final DateTime lastUpdated) throws APIException {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", "id,created,lastUpdated,name,displayName");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<DashboardContent> actualItems
                = getApiResourceByType(type, QUERY_MAP_BASIC);

        List<DashboardContent> updatedItems =
                getApiResourceByType(type, QUERY_MAP_FULL);
        if (updatedItems != null && !updatedItems.isEmpty()) {
            for (DashboardContent element : updatedItems) {
                element.setType(type);
            }
        }


        List<DashboardContent> persistedItems =
                dashboardItemContentStore.queryByTypes(Arrays.asList(type));

        return merge(actualItems, updatedItems, persistedItems);
    }

    private List<DashboardContent> getApiResourceByType(String type, Map<String, String> queryParams) throws APIException {
        switch (type) {
            case DashboardContent.TYPE_CHART:
                return unwrapResponse(dhisApi.getCharts(queryParams), "charts");
            case DashboardContent.TYPE_EVENT_CHART:
                return unwrapResponse(dhisApi.getEventCharts(queryParams), "eventCharts");
            case DashboardContent.TYPE_MAP:
                return unwrapResponse(dhisApi.getMaps(queryParams), "maps");
            case DashboardContent.TYPE_REPORT_TABLE:
                return unwrapResponse(dhisApi.getReportTables(queryParams), "reportTables");
            case DashboardContent.TYPE_EVENT_REPORT:
                return unwrapResponse(dhisApi.getEventReports(queryParams), "eventReports");
            case DashboardContent.TYPE_USERS:
                return unwrapResponse(dhisApi.getUsers(queryParams), "users");
            case DashboardContent.TYPE_REPORTS:
                return unwrapResponse(dhisApi.getReports(queryParams), "reports");
            case DashboardContent.TYPE_RESOURCES:
                return unwrapResponse(dhisApi.getResources(queryParams), "documents");
            default:
                throw new IllegalArgumentException("Unsupported DashboardContent type");
        }
    }
}