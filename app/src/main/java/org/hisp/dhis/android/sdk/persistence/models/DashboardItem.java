/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.meta.State;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

@Table(databaseName = Dhis2Database.NAME)
public final class DashboardItem extends BaseMetaDataObject {
    public static final int MAX_CONTENT = 8;
    public static final String SHAPE_NORMAL = "normal";
    public static final String SHAPE_DOUBLE_WIDTH = "double_width";
    public static final String SHAPE_FULL_WIDTH = "full_width";
    private static final String TAG = DashboardItem.class.getSimpleName();
    @JsonIgnore
    @Column(name = "state")
    @NotNull
    State state;

    @JsonProperty("type")
    @Column(name = "type")
    String type;

    @JsonProperty("shape")
    @Column(name = "shape")
    String shape;

    @JsonIgnore
    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "dashboard", columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
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
        state = State.SYNCED;
        shape = SHAPE_NORMAL;
    }

    /**
     * Factory method which creates and returns DashboardItem.
     *
     * @param dashboard Dashboard to associate with item.
     * @param content   Content for dashboard item.
     * @return new item.
     */
    @JsonIgnore
    public static DashboardItem createDashboardItem(Dashboard dashboard,
                                                    DashboardItemContent content) {
        DateTime lastUpdatedDateTime = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);

        DashboardItem item = new DashboardItem();
        item.setCreated(lastUpdatedDateTime.toString());
        item.setLastUpdated(lastUpdatedDateTime.toString());
        item.setState(State.TO_POST);
        item.setDashboard(dashboard);
        item.setAccess(Access.provideDefaultAccess());
        item.setType(content.getType());

        return item;
    }

    /**
     * This method will change the state of the model to TO_DELETE
     * if the model was already synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from embedded database.
     */
    @JsonIgnore
    public void deleteDashboardItem() {
        if (state == State.TO_POST) {
            super.delete();
        } else {
            state = State.TO_DELETE;
            super.save();
        }
    }

    /**
     * Returns related to item DashboardElements.
     *
     * @return list of dashboard elements
     */
    @JsonIgnore
    public long getContentCount() {
        List<DashboardElement> elements = new Select().from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table.DASHBOARDITEM_DASHBOARDITEM).is(getUid()))
                .and(Condition.column(DashboardElement$Table.STATE).isNot(State.TO_DELETE.toString()))
                .queryList();
        return elements == null ? 0 : elements.size();
    }

    @JsonIgnore
    public List<DashboardElement> queryRelatedDashboardElements() {
        if (isEmpty(getType())) {
            return new ArrayList<>();
        }

        List<DashboardElement> elements = new Select().from(DashboardElement.class)
                .where(Condition.column(DashboardElement$Table.DASHBOARDITEM_DASHBOARDITEM).is(getUid()))
                .and(Condition.column(DashboardElement$Table.STATE).isNot(State.TO_DELETE.toString()))
                .queryList();

        if (elements == null) {
            elements = new ArrayList<>();
        }

        return elements;
    }

    /**
     * Convenience method for retrieving DashboardElements from item.
     *
     * @return dashboard elements.
     */
    @JsonIgnore
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

    @JsonIgnore
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


    /////////////////////////////////////////////////////////////////////////
    // Getters and setters
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getShape() {
        return shape;
    }

    @JsonIgnore
    public void setShape(String shape) {
        this.shape = shape;
    }

    @JsonIgnore
    public Dashboard getDashboard() {
        return dashboard;
    }

    @JsonIgnore
    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    /////////////////////////////////////////////////////////////////////////
    // Getters and setters for link type elements
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public List<DashboardElement> getUsers() {
        return users;
    }

    @JsonIgnore
    public void setUsers(List<DashboardElement> users) {
        this.users = users;
    }

    @JsonIgnore
    public List<DashboardElement> getReports() {
        return reports;
    }

    @JsonIgnore
    public void setReports(List<DashboardElement> reports) {
        this.reports = reports;
    }

    @JsonIgnore
    public List<DashboardElement> getResources() {
        return resources;
    }

    @JsonIgnore
    public void setResources(List<DashboardElement> resources) {
        this.resources = resources;
    }

    /////////////////////////////////////////////////////////////////////////
    // Getters and setters for embedded type elements
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public DashboardElement getChart() {
        return chart;
    }

    @JsonIgnore
    public void setChart(DashboardElement chart) {
        this.chart = chart;
    }

    @JsonIgnore
    public DashboardElement getEventChart() {
        return eventChart;
    }

    @JsonIgnore
    public void setEventChart(DashboardElement eventChart) {
        this.eventChart = eventChart;
    }

    @JsonIgnore
    public DashboardElement getReportTable() {
        return reportTable;
    }

    @JsonIgnore
    public void setReportTable(DashboardElement reportTable) {
        this.reportTable = reportTable;
    }

    @JsonIgnore
    public DashboardElement getMap() {
        return map;
    }

    @JsonIgnore
    public void setMap(DashboardElement map) {
        this.map = map;
    }

    @JsonIgnore
    public DashboardElement getEventReport() {
        return eventReport;
    }

    @JsonIgnore
    public void setEventReport(DashboardElement eventReport) {
        this.eventReport = eventReport;
    }

    @JsonIgnore
    public boolean isMessages() {
        return messages;
    }

    @JsonIgnore
    public void setMessages(boolean messages) {
        this.messages = messages;
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }
}