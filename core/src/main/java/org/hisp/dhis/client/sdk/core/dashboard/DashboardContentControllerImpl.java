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
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;

import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbUtils;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.utils.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DashboardContentControllerImpl extends AbsDataController<DashboardContent>
        implements DashboardContentController {

    /* Controllers */
    private final SystemInfoController systemInfoController;

    /* Persistence */
    private final DashboardContentStore dashboardContentStore;
    private final StateStore stateStore;

    /* dashboard client */
    private final DashboardApiClient dashboardApiClient;

    /* last updated preferences */
    private final LastUpdatedPreferences lastUpdatedPreferences;

    /* database transaction manager */
    private final TransactionManager transactionManager;

    public DashboardContentControllerImpl(SystemInfoController systemInfoController,
                                          DashboardContentStore dashboardContentStore,
                                          StateStore stateStore,
                                          DashboardApiClient dashboardApiClient,
                                          LastUpdatedPreferences lastUpdatedPreferences,
                                          TransactionManager transactionManager, Logger logger) {
        super(logger, dashboardContentStore);

        this.systemInfoController = systemInfoController;
        this.dashboardContentStore = dashboardContentStore;
        this.stateStore = stateStore;
        this.dashboardApiClient = dashboardApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.transactionManager = transactionManager;
    }

    @Override
    public void syncDashboardContent(SyncStrategy syncStrategy) throws ApiException {
        DateTime lastUpdated = lastUpdatedPreferences.get(ResourceType.DASHBOARDS_CONTENT, DateType.SERVER);
        DateTime serverTime = systemInfoController.getSystemInfo().getServerDate();
        /* first we need to update api resources, dashboards
        and dashboard items */
        List<DashboardContent> dashboardContent = updateApiResources(lastUpdated);
        logger.d("dashboardContent", dashboardContent.toString());

        List<DbOperation> operations = new ArrayList<>();

        operations.addAll(DbUtils.createOperations(dashboardContentStore,
                dashboardContentStore.queryAll(), dashboardContent));

        transactionManager.transact(operations);
        lastUpdatedPreferences.save(ResourceType.DASHBOARDS_CONTENT, DateType.SERVER, serverTime);
    }

    // TODO
    @Override
    public void pull(SyncStrategy syncStrategy) throws ApiException {

    }

    // TODO
    @Override
    public void pull(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {

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
        logger.d("dashboardContentActualItems", actualItems.toString());


        List<DashboardContent> updatedItems =
                getApiResourceByType(type, QUERY_MAP_FULL);
        logger.d("dashboardContentUpdatedItems", updatedItems.toString());

        if (updatedItems != null && !updatedItems.isEmpty()) {
            for (DashboardContent element : updatedItems) {
                element.setType(type);
            }
        }

        Set<String> types = new HashSet<>();
        types.add(type);

        List<DashboardContent> persistedItems =
                dashboardContentStore.queryByType(type);
        if(persistedItems!=null){
            logger.d("dashboardContentPersisted", persistedItems.toString());
        }else{
            logger.d("dashboardContentPersisted", "empty persisted");
        }

        return ModelUtils.merge(actualItems, updatedItems, persistedItems);
    }

    private List<DashboardContent> getApiResourceByType(
            String type, Map<String, String> queryParams) {

        switch (type) {
            case DashboardContent.TYPE_CHART:
                return dashboardApiClient.getBaseCharts(queryParams);
            case DashboardContent.TYPE_EVENT_CHART:
                return dashboardApiClient.getBaseEventCharts(queryParams);
            case DashboardContent.TYPE_MAP:
                return dashboardApiClient.getBaseMaps(queryParams);
            case DashboardContent.TYPE_REPORT_TABLE:
                return dashboardApiClient.getBaseReportTables(queryParams);
            case DashboardContent.TYPE_EVENT_REPORT:
                return dashboardApiClient.getBaseEventReports(queryParams);
            case DashboardContent.TYPE_USERS:
                return dashboardApiClient.getBaseUsers(queryParams);
            case DashboardContent.TYPE_REPORTS:
                return dashboardApiClient.getBaseReports(queryParams);
            case DashboardContent.TYPE_RESOURCES:
                return dashboardApiClient.getBaseResources(queryParams);
            default:
                throw new IllegalArgumentException("Unsupported DashboardContent type");
        }
    }
}