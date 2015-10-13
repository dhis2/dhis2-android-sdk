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

package org.hisp.dhis.android.sdk.network.dashboard;

import org.hisp.dhis.android.sdk.corejava.common.network.Response;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardApiClient;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.network.retrofit.IDhisApi;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.network.utils.NetworkUtils.call;
import static org.hisp.dhis.android.sdk.network.utils.NetworkUtils.unwrap;

public class DashboardApiClient implements IDashboardApiClient {
    private final IDashboardRetrofitClient dhisApi;

    public DashboardApiClient(IDashboardRetrofitClient dhisApi) {
        this.dhisApi = dhisApi;
    }

    @Override
    public List<Dashboard> getBasicDashboards(DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<Dashboard> actualDashboards = unwrap(call(dhisApi
                .getDashboards(QUERY_MAP_BASIC)), IDhisApi.DASHBOARDS);
        if (actualDashboards == null) {
            actualDashboards = new ArrayList<>();
        }

        return actualDashboards;
    }

    @Override
    public List<Dashboard> getFullDashboards(DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access";

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

        List<Dashboard> dashboards = unwrap(call(dhisApi
                .getDashboards(QUERY_MAP_FULL)), IDhisApi.DASHBOARDS);

        if (dashboards == null) {
            dashboards = new ArrayList<>();
        }

        // Building dashboard item to dashboard relationship.
        if (!dashboards.isEmpty()) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard == null || dashboard.getDashboardItems().isEmpty()) {
                    continue;
                }

                for (DashboardItem item : dashboard.getDashboardItems()) {
                    item.setDashboard(dashboard);
                }
            }
        }

        return dashboards;
    }

    @Override
    public List<DashboardItem> getBasicDashboardItems(DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id,created,lastUpdated,shape");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        List<DashboardItem> updatedItems = unwrap(call(dhisApi
                .getDashboardItems(QUERY_MAP_BASIC)), IDhisApi.DASHBOARD_ITEMS);

        if (updatedItems == null) {
            updatedItems = new ArrayList<>();
        }

        return updatedItems;
    }

    @Override
    public Dashboard getBasicDashboardByUid(String uid) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        return call(dhisApi.getDashboard(uid, QUERY_PARAMS));
    }

    @Override
    public Response postDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    public Response postDashboardItem(DashboardItem dashboardItem) {
        return null;
    }

    @Override
    public Response putDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    public Response deleteDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    public Response deleteDashboardItem(DashboardItem dashboardItem) {
        return null;
    }

    @Override
    public Response deleteDashboardItemContent(DashboardElement dashboardElement) {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicCharts() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicEventCharts() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicMaps() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicReportTables() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicEventReports() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicUsers() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicReports() {
        return null;
    }

    @Override
    public List<DashboardContent> getBasicResources() {
        return null;
    }

    @Override
    public Response getReportTableDataByUid(String uid) {
        return null;
    }
}
