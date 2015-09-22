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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.models.common.meta.Action;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.StringUtils.isEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardItem extends BaseIdentifiableObject {
    public static final int MAX_CONTENT = 8;

    public static final String SHAPE_NORMAL = "normal";
    public static final String SHAPE_DOUBLE_WIDTH = "double_width";
    public static final String SHAPE_FULL_WIDTH = "full_width";

    @JsonIgnore
    Action action;

    @JsonProperty("type")
    String type;

    @JsonProperty("shape")
    String shape;

    @JsonIgnore
    Dashboard dashboard;

    // DashboardElements
    @JsonProperty("chart")
    DashboardElement chart;

    @JsonProperty("eventChart")
    DashboardElement eventChart;

    @JsonProperty("map")
    DashboardElement map;

    @JsonProperty("reportTable")
    DashboardElement reportTable;

    @JsonProperty("eventReport")
    DashboardElement eventReport;

    @JsonProperty("users")
    List<DashboardElement> users;

    @JsonProperty("reports")
    List<DashboardElement> reports;

    @JsonProperty("resources")
    List<DashboardElement> resources;

    @JsonProperty("messages")
    boolean messages;

    public DashboardItem() {
        action = Action.SYNCED;
        shape = SHAPE_NORMAL;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardElement getChart() {
        return chart;
    }

    public void setChart(DashboardElement chart) {
        this.chart = chart;
    }

    public DashboardElement getEventChart() {
        return eventChart;
    }

    public void setEventChart(DashboardElement eventChart) {
        this.eventChart = eventChart;
    }

    public DashboardElement getMap() {
        return map;
    }

    public void setMap(DashboardElement map) {
        this.map = map;
    }

    public DashboardElement getReportTable() {
        return reportTable;
    }

    public void setReportTable(DashboardElement reportTable) {
        this.reportTable = reportTable;
    }

    public DashboardElement getEventReport() {
        return eventReport;
    }

    public void setEventReport(DashboardElement eventReport) {
        this.eventReport = eventReport;
    }

    public List<DashboardElement> getUsers() {
        return users;
    }

    public void setUsers(List<DashboardElement> users) {
        this.users = users;
    }

    public List<DashboardElement> getReports() {
        return reports;
    }

    public void setReports(List<DashboardElement> reports) {
        this.reports = reports;
    }

    public List<DashboardElement> getResources() {
        return resources;
    }

    public void setResources(List<DashboardElement> resources) {
        this.resources = resources;
    }

    public boolean isMessages() {
        return messages;
    }

    public void setMessages(boolean messages) {
        this.messages = messages;
    }

    public void setDashboardElements(List<DashboardElement> dashboardElements) {
        if (isEmpty(getType())) {
            return;
        }

        if (dashboardElements == null || dashboardElements.isEmpty()) {
            return;
        }

        switch (getType()) {
            case DashboardItemContent.TYPE_CHART: {
                setChart(dashboardElements.get(0));
                break;
            }
            case DashboardItemContent.TYPE_EVENT_CHART: {
                setEventChart(dashboardElements.get(0));
                break;
            }
            case DashboardItemContent.TYPE_MAP: {
                setMap(dashboardElements.get(0));
                break;
            }
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                setReportTable(dashboardElements.get(0));
                break;
            }
            case DashboardItemContent.TYPE_EVENT_REPORT: {
                setEventReport(dashboardElements.get(0));
                break;
            }
            case DashboardItemContent.TYPE_USERS: {
                setUsers(dashboardElements);
                break;
            }
            case DashboardItemContent.TYPE_REPORTS: {
                setReports(dashboardElements);
                break;
            }
            case DashboardItemContent.TYPE_RESOURCES: {
                setResources(dashboardElements);
                break;
            }
        }
    }

    public List<DashboardElement> getDashboardElements() {

        List<DashboardElement> elements = new ArrayList<>();
        if (isEmpty(getType())) {
            return elements;
        }

        switch (getType()) {
            case DashboardItemContent.TYPE_CHART: {
                elements.add(getChart());
                break;
            }
            case DashboardItemContent.TYPE_EVENT_CHART: {
                elements.add(getEventChart());
                break;
            }
            case DashboardItemContent.TYPE_MAP: {
                elements.add(getMap());
                break;
            }
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                elements.add(getReportTable());
                break;
            }
            case DashboardItemContent.TYPE_EVENT_REPORT: {
                elements.add(getEventReport());
                break;
            }
            case DashboardItemContent.TYPE_USERS: {
                elements.addAll(getUsers());
                break;
            }
            case DashboardItemContent.TYPE_REPORTS: {
                elements.addAll(getReports());
                break;
            }
            case DashboardItemContent.TYPE_RESOURCES: {
                elements.addAll(getResources());
                break;
            }
        }

        return elements;
    }
}