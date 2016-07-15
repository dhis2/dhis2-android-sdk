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

import org.hisp.dhis.client.sdk.core.common.network.Response;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

// TODO build relationships between (dashbord and dashboard items for example) models.
// TODO Handle field filtering and selection.
// TODO do not return null collections.
public interface DashboardApiClient {

    List<Dashboard> getDashboardUids(DateTime lastUpdated);

    List<Dashboard> getDashboards(DateTime lastUpdated);

    List<DashboardItem> getBaseDashboardItems(DateTime lastUpdated);

    List<DashboardItem> getDashboardItems(DateTime lastUpdated);

    Dashboard getBaseDashboardByUid(String uid);

    Response postDashboard(Dashboard dashboard);

    Response postDashboardItem(DashboardItem dashboardItem);

    Response putDashboard(Dashboard dashboard);

    Response deleteDashboard(Dashboard dashboard);

    Response deleteDashboardItem(DashboardItem dashboardItem);

    Response deleteDashboardItemContent(DashboardElement dashboardElement);

    List<DashboardContent> getBaseCharts(Map<String, String> queryParams);

    List<DashboardContent> getBaseEventCharts(Map<String, String> queryParams);

    List<DashboardContent> getBaseMaps(Map<String, String> queryParams);

    List<DashboardContent> getBaseReportTables(Map<String, String> queryParams);

    List<DashboardContent> getBaseEventReports(Map<String, String> queryParams);

    List<DashboardContent> getBaseUsers(Map<String, String> queryParams);

    List<DashboardContent> getBaseReports(Map<String, String> queryParams);

    List<DashboardContent> getBaseResources(Map<String, String> queryParams);

    Response getReportTableDataByUid(String uid);
}
