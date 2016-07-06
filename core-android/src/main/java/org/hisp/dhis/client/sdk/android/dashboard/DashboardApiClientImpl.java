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

package org.hisp.dhis.client.sdk.android.dashboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.network.DhisApi;
import org.hisp.dhis.client.sdk.core.common.network.Response;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardApiClient;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.call;
import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.unwrap;


public class DashboardApiClientImpl implements DashboardApiClient {
    private final DashboardApiClientRetrofit dashboardApiClientRetrofit;

    public DashboardApiClientImpl(@NonNull DashboardApiClientRetrofit dashboardApiClientRetrofit) {
        this.dashboardApiClientRetrofit = dashboardApiClientRetrofit;
    }

    @Override
    @NonNull
    public List<Dashboard> getDashboardUids(@Nullable DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("lastUpdated:gt:" , lastUpdated.toString());
        }

        return unwrap(call(dashboardApiClientRetrofit.getDashboards(QUERY_MAP_BASIC)), DhisApi.DASHBOARDS);
    }

    @Override
    @NonNull
    public List<Dashboard> getDashboards(@Nullable DateTime lastUpdated) {
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

        List<Dashboard> dashboards = unwrap(call(dashboardApiClientRetrofit.getDashboards(QUERY_MAP_FULL)),
                DhisApi.DASHBOARDS);

        // Building dashboard item to dashboard relationship.
        for (Dashboard dashboard : dashboards) {
            if (dashboard != null && dashboard.getDashboardItems() != null &&
                    dashboard.getDashboardItems().isEmpty()) {
                for (DashboardItem item : dashboard.getDashboardItems()) {
                    item.setDashboard(dashboard);
                }
            }
        }

        return dashboards;
    }

    @Override
    @NonNull
    public List<DashboardItem> getBaseDashboardItems(@Nullable DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        QUERY_MAP_BASIC.put("fields", "id,created,lastUpdated,shape");

        if (lastUpdated != null) {
            QUERY_MAP_BASIC.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        return unwrap(call(dashboardApiClientRetrofit.getDashboardItems(QUERY_MAP_BASIC)), DhisApi.DASHBOARD_ITEMS);
    }

    @Override
    @NonNull
    public List<DashboardItem> getDashboardItems(@Nullable DateTime dateTime) {
        return null;
    }

    @Override
    @NonNull
    public Dashboard getBaseDashboardByUid(String uid) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        return call(dashboardApiClientRetrofit.getDashboard(uid, QUERY_PARAMS));
    }

    @Override
    @NonNull
    public Response postDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    @NonNull
    public Response postDashboardItem(DashboardItem dashboardItem) {
        return null;
    }

    @Override
    @NonNull
    public Response putDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    @NonNull
    public Response deleteDashboard(Dashboard dashboard) {
        return null;
    }

    @Override
    @NonNull
    public Response deleteDashboardItem(DashboardItem dashboardItem) {
        return null;
    }

    @Override
    @NonNull
    public Response deleteDashboardItemContent(DashboardElement dashboardElement) {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseCharts() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseEventCharts() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseMaps() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseReportTables() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseEventReports() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseUsers() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseReports() {
        return null;
    }

    @Override
    @NonNull
    public List<DashboardContent> getBaseResources() {
        return null;
    }

    @Override
    @NonNull
    public Response getReportTableDataByUid(String uid) {
        return null;
    }
}
