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

package org.hisp.dhis.java.sdk.dashboard;

import org.hisp.dhis.java.sdk.common.network.Response;
import org.hisp.dhis.java.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardItem;
import org.joda.time.DateTime;

import java.util.List;

// TODO build relationships between (dashbord and dashboard items for example) models.
// TODO Handle field filtering and selection.
// TODO do not return null collections.
public interface IDashboardApiClient {

    List<Dashboard> getBasicDashboards(DateTime lastUpdated);

    List<Dashboard> getFullDashboards(DateTime lastUpdated);

    List<DashboardItem> getBasicDashboardItems(DateTime lastUpdated);

    Dashboard getBasicDashboardByUid(String uid);

    Response postDashboard(Dashboard dashboard);

    Response postDashboardItem(DashboardItem dashboardItem);

    Response putDashboard(Dashboard dashboard);

    Response deleteDashboard(Dashboard dashboard);

    Response deleteDashboardItem(DashboardItem dashboardItem);

    Response deleteDashboardItemContent(DashboardElement dashboardElement);

    List<DashboardContent> getBasicCharts();

    List<DashboardContent> getBasicEventCharts();

    List<DashboardContent> getBasicMaps();

    List<DashboardContent> getBasicReportTables();

    List<DashboardContent> getBasicEventReports();

    List<DashboardContent> getBasicUsers();

    List<DashboardContent> getBasicReports();

    List<DashboardContent> getBasicResources();

    Response getReportTableDataByUid(String uid);
}
