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
import org.hisp.dhis.client.sdk.core.common.controllers.AbsDataController;
import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.Response;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperationImpl;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class DashboardControllerImpl extends AbsDataController<Dashboard> implements DashboardController {

    /* Controllers */
    private final SystemInfoController systemInfoController;

    /* Persistence */
    private final DashboardStore dashboardStore;
    private final DashboardItemStore dashboardItemStore;
    private final DashboardElementStore dashboardElementStore;
    private final DashboardContentStore dashboardItemContentStore;
    private final StateStore stateStore;

    /* dashboard client */
    private final DashboardApiClient dashboardApiClient;
    private final SystemInfoApiClient systemInfoApiClient;

    /* last updated preferences */
    private final LastUpdatedPreferences lastUpdatedPreferences;

    /* database transaction manager */
    private final TransactionManager transactionManager;

    public DashboardControllerImpl(SystemInfoController systemInfoController,
                                   DashboardStore dashboardStore,
                                   DashboardItemStore dashboardItemStore,
                                   DashboardElementStore dashboardElementStore,
                                   DashboardContentStore dashboardItemContentStore,
                                   StateStore stateStore,
                                   DashboardApiClient dashboardApiClient,
                                   SystemInfoApiClient systemInfoApiClient,
                                   LastUpdatedPreferences lastUpdatedPreferences,
                                   TransactionManager transactionManager, Logger logger) {
        super(logger, dashboardStore);

        this.systemInfoController = systemInfoController;
        this.dashboardStore = dashboardStore;
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemContentStore = dashboardItemContentStore;
        this.stateStore = stateStore;
        this.dashboardApiClient = dashboardApiClient;
        this.systemInfoApiClient = systemInfoApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.transactionManager = transactionManager;
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

    @Override
    public void syncDashboards(SyncStrategy syncStrategy) throws ApiException {
        /* first we need to fetch all changes from server
        and apply them to local database */
        getDashboardDataFromServer();

        /* now we can try to send changes made locally to server */
        sendLocalChanges();
    }

    @Override
    public void pull(SyncStrategy syncStrategy) throws ApiException {
        /* first we need to fetch all changes from server and apply them to local database */
        getDashboardDataFromServer();

        /* now we can try to send changes made locally to server */
        // sendLocalChanges();

        /* pull content */
        // syncDashboardContent();
    }

    @Override
    public void pull(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {

    }

    private void getDashboardDataFromServer() {
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.DASHBOARDS, DateType.SERVER);
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();

        List<Dashboard> dashboards = updateDashboards(lastUpdated);
        List<DashboardItem> dashboardItems = updateDashboardItems(dashboards, lastUpdated);

        Queue<DbOperation> operations = new LinkedList<>();

        operations.addAll(DbUtils.createOperations(dashboardStore,
                stateStore.queryModelsWithActions(Dashboard.class, Action.SYNCED, Action
                        .TO_UPDATE, Action.TO_DELETE), dashboards));
        operations.addAll(DbUtils.createOperations(dashboardItemStore,
                stateStore.queryModelsWithActions(DashboardItem.class, Action.SYNCED, Action
                        .TO_UPDATE, Action.TO_DELETE), dashboardItems));
        operations.addAll(createOperations(dashboardItems));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(ResourceType.DASHBOARDS, DateType.SERVER, serverTime);
    }

    private List<Dashboard> updateDashboards(DateTime lastUpdated) {
        // List of dashboards with UUIDs (without content). This list is used
        // only to determine what was removed on server.
        List<Dashboard> actualDashboards = dashboardApiClient.getDashboardUids(null);

        // List of updated dashboards with content.
        // TODO
        List<Dashboard> updatedDashboards = dashboardApiClient.getDashboards(lastUpdated);

        // List of persisted dashboards.
        List<Dashboard> persistedDashboards = stateStore.queryModelsWithActions(Dashboard.class,
                Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE);

        Map<Long, List<DashboardItem>> dashboardItemMap = getDashboardItemMap();
        Map<Long, List<DashboardElement>> dashboardElementMap = getDashboardElementMap(false);

        if(persistedDashboards!=null && !persistedDashboards.isEmpty()) {
            for (Dashboard dashboard : persistedDashboards) {
                List<DashboardItem> items = dashboardItemMap.get(dashboard.getId());
                if (items == null || items.isEmpty()) {
                    continue;
                }

                for (DashboardItem item : items) {
                    item.setDashboardElements(dashboardElementMap.get(item.getId()));
                }
                dashboard.setDashboardItems(items);
            }
        }

        return ModelUtils.merge(actualDashboards, updatedDashboards, persistedDashboards);
    }

    private List<DashboardItem> updateDashboardItems(List<Dashboard> dashboards, DateTime
            lastUpdated) {
        // List of actual dashboard items.
        List<DashboardItem> actualItems = new ArrayList<>();
        for (Dashboard dashboard : dashboards) {
            List<DashboardItem> items = dashboard.getDashboardItems();
            actualItems.addAll(items != null ? items : new ArrayList<DashboardItem>());
        }

        // List of persisted dashboard items
        Map<String, DashboardItem> persistedDashboardItems =
                ModelUtils.toMap(stateStore.queryModelsWithActions(DashboardItem.class,
                        Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE));

        // List of updated dashboard items. We need this only to get
        // information about updates of item shape.
        List<DashboardItem> updatedItems = dashboardApiClient.getBaseDashboardItems(lastUpdated);

        // Map of items where keys are UUIDs.
        Map<String, DashboardItem> updatedItemsMap = ModelUtils.toMap(updatedItems);

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

    private List<DbOperationImpl> createOperations(List<DashboardItem> refreshedItems) {
        List<DbOperationImpl> dbOperations = new ArrayList<>();

        Map<Long, List<DashboardElement>> dashboardElementMap = getDashboardElementMap(false);
        for (DashboardItem refreshedItem : refreshedItems) {
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

            List<String> persistedElementIds = ModelUtils.toUidList(persistedElementList);
            List<String> refreshedElementIds = ModelUtils.toUidList(refreshedElementList);

            List<String> itemIdsToInsert = subtract(refreshedElementIds, persistedElementIds);
            List<String> itemIdsToDelete = subtract(persistedElementIds, refreshedElementIds);

            for (String elementToDelete : itemIdsToDelete) {
                int index = persistedElementIds.indexOf(elementToDelete);
                DashboardElement element = persistedElementList.get(index);
                dbOperations.add(DbOperationImpl
                        .with(dashboardElementStore)
                        .delete(element));

                persistedElementIds.remove(index);
                persistedElementList.remove(index);
            }

            for (String elementToInsert : itemIdsToInsert) {
                int index = refreshedElementIds.indexOf(elementToInsert);
                DashboardElement dashboardElement = refreshedElementList.get(index);
                dbOperations.add(DbOperationImpl
                        .with(dashboardElementStore)
                        .insert(dashboardElement));

                refreshedElementIds.remove(index);
                refreshedElementList.remove(index);
            }
        }

        return dbOperations;
    }

    // TODO move this method out
    private Map<Long, List<DashboardItem>> getDashboardItemMap() {
        List<DashboardItem> dashboardItemsList = stateStore
                .queryModelsWithActions(DashboardItem.class, Action.SYNCED, Action.TO_UPDATE,
                        Action.TO_DELETE);
        Map<Long, List<DashboardItem>> dashboardItemMap = new HashMap<>();

        if(dashboardItemsList!=null && !dashboardItemsList.isEmpty()) {
            for (DashboardItem dashboardItem : dashboardItemsList) {
                Long dashboardId = dashboardItem.getDashboard().getId();

                List<DashboardItem> bag = dashboardItemMap.get(dashboardId);
                if (bag == null) {
                    bag = new ArrayList<>();
                    dashboardItemMap.put(dashboardId, bag);
                }

                bag.add(dashboardItem);
            }
        }

        return dashboardItemMap;
    }

    // TODO move this method out
    private Map<Long, List<DashboardElement>> getDashboardElementMap(boolean withAction) {
        List<DashboardElement> dashboardElementsList;

        if (withAction) {
            dashboardElementsList = stateStore.queryModelsWithActions(
                    DashboardElement.class, Action.TO_POST);
        } else {
            dashboardElementsList = stateStore.queryModelsWithActions(
                    DashboardElement.class, Action.SYNCED, Action.TO_UPDATE, Action.TO_DELETE);
        }
        Map<Long, List<DashboardElement>> dashboardElementMap = new HashMap<>();

        if(dashboardElementsList!=null && !dashboardElementsList.isEmpty()) {
            for (DashboardElement dashboardElement : dashboardElementsList) {
                Long dashboardItemId = dashboardElement.getDashboardItem().getId();

                List<DashboardElement> bag = dashboardElementMap.get(dashboardItemId);
                if (bag == null) {
                    bag = new ArrayList<>();
                    dashboardElementMap.put(dashboardItemId, bag);
                }

                bag.add(dashboardElement);
            }
        }

        return dashboardElementMap;
    }


    private void sendLocalChanges() {
        sendDashboardChanges();
        sendDashboardItemChanges();
        sendDashboardElements();
    }

    private void sendDashboardChanges() {
        // we need to sort dashboards in natural order.
        // In order they were inserted in local database.

        // List<Dashboard> dashboards = dashboardStore.filter(Action.SYNCED);
        List<Dashboard> dashboards = stateStore
                .queryModelsWithActions(Dashboard.class, Action.TO_POST, Action.TO_UPDATE, Action
                        .TO_DELETE);
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

    private void postDashboard(Dashboard dashboard) {
        try {
            // Response response = dhisApi.postDashboard(dashboard);
            Response response = dashboardApiClient.postDashboard(dashboard);
            // also, we will need to find UUID of newly created dashboard,
            // which is contained inside of HTTP Location header

            // Header header = findLocationHeader(response.getHeaders());

            // parse the value of header as URI and extract the id

            /* String dashboardId = Uri.parse(header.getValue()).getLastPathSegment(); */

            // set UUID, change state and save dashboard

            // dashboard.setUId(dashboardId);

            // dashboard.setAction(Action.SYNCED);

            dashboardStore.save(dashboard);
            stateStore.saveActionForModel(dashboard, Action.SYNCED);

            updateDashboardTimeStamp(dashboard);
        } catch (ApiException apiException) {
            // handleApiException(apiException);
        }
    }

    private void putDashboard(Dashboard dashboard) {
        try {
            dashboardApiClient.putDashboard(dashboard);

            // dashboard.setAction(Action.SYNCED);
            dashboardStore.save(dashboard);
            stateStore.saveActionForModel(dashboard, Action.SYNCED);

            updateDashboardTimeStamp(dashboard);
        } catch (ApiException apiException) {
            // handleApiException(apiException, dashboard, dashboardStore);
        }
    }

    private void deleteDashboard(Dashboard dashboard) {
        try {
            // dhisApi.deleteDashboard(dashboard.getUId());
            dashboardApiClient.deleteDashboard(dashboard);

            dashboardStore.delete(dashboard);
        } catch (ApiException apiException) {
            // handleApiException(apiException, dashboard, dashboardStore);
        }
    }

    private void sendDashboardItemChanges() {
        /* List<DashboardItem> dashboardItems =
                dashboardItemStore.filter(Action.SYNCED); */
        List<DashboardItem> dashboardItems =
                stateStore.queryModelsWithActions(DashboardItem.class, Action.TO_POST, Action
                        .TO_UPDATE, Action.TO_DELETE);
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

    private void postDashboardItem(DashboardItem dashboardItem) {
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
                // Response response = dashboardApiClient.postDashboardItem(dashboard.getUId(),
                // dashboardItem.getType(), element.getUId(), "");

                // instead, post element
                Response response = dashboardApiClient.postDashboardItem(dashboardItem);

                // Header locationHeader = findLocationHeader(response.getHeaders());

                /* String dashboardItemUId = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment(); */

                /* dashboardItem.setUId(dashboardItemUId); */

                // dashboardItem.setAction(Action.SYNCED);

                // element.setAction(Action.SYNCED);

                dashboardItemStore.save(dashboardItem);
                dashboardElementStore.save(element);

                stateStore.saveActionForModel(dashboardItem, Action.SYNCED);
                stateStore.saveActionForModel(dashboard, Action.SYNCED);

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (ApiException apiException) {
                // handleApiException(apiException, dashboardItem, dashboardItemStore);
            }
        }
    }

    private void deleteDashboardItem(DashboardItem dashboardItem) {
        Dashboard dashboard = dashboardItem.getDashboard();
        Action dashboardAction = stateStore.queryActionForModel(dashboard);

        if (dashboard != null && dashboardAction != null) {
            boolean isDashboardSynced = (dashboardAction.equals(Action.SYNCED) ||
                    dashboardAction.equals(Action.TO_UPDATE));

            if (!isDashboardSynced) {
                return;
            }

            try {
                /* dhisApi.deleteDashboardItem(dashboard.getUId(),
                        dashboardItem.getUId()); */
                dashboardApiClient.deleteDashboardItem(dashboardItem);
                dashboardItemStore.delete(dashboardItem);

                // we have to update timestamp of dashboard after adding new item.
                updateDashboardTimeStamp(dashboardItem.getDashboard());
            } catch (ApiException apiException) {
                // handleApiException(apiException, dashboardItem, dashboardItemStore);
            }
        }
    }

    private void sendDashboardElements() {
        /* List<DashboardElement> elements = new Select()
                .from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table
                        .STATE).isNot(Action.SYNCED))
                .orderBy(true, DashboardElement$Table.ID)
                .queryList(); */
        List<DashboardElement> elements = stateStore
                .queryModelsWithActions(DashboardElement.class, Action.TO_POST, Action.TO_UPDATE,
                        Action.TO_DELETE);
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

    private void postDashboardElement(DashboardElement element) {
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

    private void deleteDashboardElement(DashboardElement element) {
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
                /* dhisApi.deleteDashboardItemContent(dashboard.getUId(),
                        item.getUId(), element.getUId()); */
                /* dashboardApiClient.deleteDashboardItemContent(dashboard.getUId(),
                        item.getUId(), element.getUId()); */
                dashboardApiClient.deleteDashboardItemContent(element);
                dashboardElementStore.delete(element);

                // removal of elements changes
                // dashboard's timestamp on server. In order to stay in pull,
                // we need to get dashboard from server.
                updateDashboardTimeStamp(item.getDashboard());
            } catch (ApiException apiException) {
                // handleApiException(apiException, element, dashboardElementStore);
            }
        }
    }

    private void updateDashboardTimeStamp(Dashboard dashboard) {
        try {
            Dashboard updatedDashboard = dashboardApiClient.getBaseDashboardByUid(dashboard
                    .getUId());

            // merging updated timestamp to local dashboard model
            dashboard.setCreated(updatedDashboard.getCreated());
            dashboard.setLastUpdated(updatedDashboard.getLastUpdated());

            dashboardStore.save(dashboard);
        } catch (ApiException apiException) {
            // handleApiException(apiException);
        }
    }

    // TODO Move this method to DashboardContentControllerImpl
    public void syncDashboardContent() {
        /* DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS_CONTENT);
        DateTime serverDateTime = dhisApi
                .getSystemInfo().getServerDate(); */

        /* first we need to update api resources, dashboards
        and dashboard items */
        /* List<DashboardContent> dashboardContent =
                updateApiResources(lastUpdated);
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(dashboardItemContentStore,
                dashboardItemContentStore.queryAll(), dashboardContent));
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.DASHBOARDS_CONTENT, serverDateTime); */
    }

    private List<DashboardContent> updateApiResources(DateTime lastUpdated) {
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

    private List<DashboardContent> updateApiResourceByType(String type, DateTime lastUpdated) {
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

        Set<String> uids = new HashSet<>();
        uids.add(type);

        List<DashboardContent> persistedItems =
                dashboardItemContentStore.queryByTypes(uids);

        return ModelUtils.merge(actualItems, updatedItems, persistedItems);
    }

    private List<DashboardContent> getApiResourceByType(
            String type, Map<String, String> queryParams) {

        switch (type) {
            case DashboardContent.TYPE_CHART:
                return dashboardApiClient.getBaseCharts();
            case DashboardContent.TYPE_EVENT_CHART:
                return dashboardApiClient.getBaseEventCharts();
            case DashboardContent.TYPE_MAP:
                return dashboardApiClient.getBaseMaps();
            case DashboardContent.TYPE_REPORT_TABLE:
                return dashboardApiClient.getBaseReportTables();
            case DashboardContent.TYPE_EVENT_REPORT:
                return dashboardApiClient.getBaseEventReports();
            case DashboardContent.TYPE_USERS:
                return dashboardApiClient.getBaseUsers();
            case DashboardContent.TYPE_REPORTS:
                return dashboardApiClient.getBaseReports();
            case DashboardContent.TYPE_RESOURCES:
                return dashboardApiClient.getBaseResources();
            default:
                throw new IllegalArgumentException("Unsupported DashboardContent type");
        }
    }

}