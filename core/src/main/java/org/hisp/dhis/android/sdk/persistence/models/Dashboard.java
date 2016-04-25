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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.meta.State;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

@Table(databaseName = Dhis2Database.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Dashboard extends BaseMetaDataObject {
    /**
     * Maximum amount of items dashboard can hold.
     */
    public static int MAX_ITEMS = 40;

    @JsonIgnore
    @Column(name = "state")
    @NotNull
    State state;

    @JsonProperty("dashboardItems")
    List<DashboardItem> dashboardItems;

    public Dashboard() {
        state = State.SYNCED;
    }

    /////////////////////////////////////////////////////////////////////////
    // Dashboard logic
    /////////////////////////////////////////////////////////////////////////

    /**
     * Creates and returns new Dashboard with given name.
     *
     * @param name Name of new dashboard.
     * @return a dashboard.
     */
    @JsonIgnore
    public static Dashboard createDashboard(String name) {
        DateTime lastUpdatedDateTime = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS);

        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_POST);
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(lastUpdatedDateTime.toString());
        dashboard.setLastUpdated(lastUpdatedDateTime.toString());
        dashboard.setAccess(Access.provideDefaultAccess());

        return dashboard;
    }

    /**
     * This method will change the name of dashboard along with the State.
     * <p/>
     * If the current state of model is State.TO_DELETE or State.TO_POST,
     * state won't be changed. Otherwise, it will be set to State.TO_UPDATE.
     *
     * @param newName New name for dashboard.
     */
    @JsonIgnore
    public void updateDashboard(String newName) {
        setName(newName);
        setDisplayName(newName);

        if (state != State.TO_DELETE && state != State.TO_POST) {
            state = State.TO_UPDATE;
        }

        super.save();
    }

    /**
     * This method will change the state of the model to State.TO_DELETE
     * if the model is synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from database.
     */
    @JsonIgnore
    public void deleteDashboard() {
        if (state == State.TO_POST) {
            super.delete();
        } else {
            state = State.TO_DELETE;
            super.save();
        }
    }

    /**
     * Returns list of DashboardItems associated with dashboard.
     * NOTE! Items will be read from database.
     *
     * @return list of items.
     */
    @JsonIgnore
    public List<DashboardItem> queryRelatedDashboardItems() {
        return new Select().from(DashboardItem.class)
                .where(Condition.column(DashboardItem$Table
                        .DASHBOARD_DASHBOARD).is(getUid()))
                .and(Condition.column(DashboardItem$Table
                        .STATE).isNot(State.TO_DELETE.toString()))
                .queryList();
    }

    /**
     * Returns an item from this dashboard of the given type which number of
     * content is less than max. Returns null if no item matches the criteria.
     *
     * @param type the type of content to return.
     * @return an item.
     */
    @JsonIgnore
    public DashboardItem getAvailableItemByType(String type) {
        List<DashboardItem> items = queryRelatedDashboardItems();

        if (items == null || items.isEmpty()) {
            return null;
        }

        for (DashboardItem item : items) {
            if (type.equals(item.getType()) &&
                    item.getContentCount() < DashboardItem.MAX_CONTENT) {
                return item;
            }
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////
    // DashboardItem logic
    /////////////////////////////////////////////////////////////////////////

    /**
     * Will try to append DashboardItemContent to current dashboard.
     * If the type of DashboardItemContent is embedded (chart, eventChart, map, eventReport, reportTable),
     * method will create a new item and append it to dashboard.
     * <p/>
     * If the type of DashboardItemContent is link type (users, reports, resources),
     * method will try to append content to existing item. Otherwise it will create a new dashboard item.
     * <p/>
     * If the overall count of items in dashboard is bigger that Dashboard.MAX_ITEMS, method will not
     * add content and return false;
     *
     * @param content
     * @return false if item count is bigger than MAX_ITEMS.
     */
    @JsonIgnore
    public boolean addItemContent(DashboardItemContent content) {
        isNull(content, "DashboardItemContent object must not be null");

        DashboardItem item;
        DashboardElement element;
        int itemsCount = getDashboardItemCount();

        if (isItemContentTypeEmbedded(content)) {
            item = DashboardItem.createDashboardItem(this, content);
            element = DashboardElement.createDashboardElement(item, content);
            itemsCount += 1;
        } else {
            item = getAvailableItemByType(content.getType());
            if (item == null) {
                item = DashboardItem.createDashboardItem(this, content);
                itemsCount += 1;
            }
            element = DashboardElement.createDashboardElement(item, content);
        }

        if (itemsCount > MAX_ITEMS) {
            return false;
        }

        item.save();
        element.save();

        return true;
    }

    private boolean isItemContentTypeEmbedded(DashboardItemContent content) {
        switch (content.getType()) {
            case DashboardItemContent.TYPE_CHART:
            case DashboardItemContent.TYPE_EVENT_CHART:
            case DashboardItemContent.TYPE_MAP:
            case DashboardItemContent.TYPE_EVENT_REPORT:
            case DashboardItemContent.TYPE_REPORT_TABLE: {
                return true;
            }
            case DashboardItemContent.TYPE_USERS:
            case DashboardItemContent.TYPE_REPORTS:
            case DashboardItemContent.TYPE_RESOURCES: {
                return false;
            }
        }

        throw new IllegalArgumentException("Unsupported DashboardItemContent type");
    }

    /////////////////////////////////////////////////////////////////////////
    // Getters and setters
    /////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public List<DashboardItem> getDashboardItems() {
        return dashboardItems;
    }

    @JsonIgnore
    public void setDashboardItems(List<DashboardItem> dashboardItems) {
        this.dashboardItems = dashboardItems;
    }

    @JsonIgnore
    public boolean hasItems() {
        return getDashboardItemCount() > 0;
    }

    @JsonIgnore
    public int getDashboardItemCount() {
        List<DashboardItem> items
                = queryRelatedDashboardItems();
        return items == null ? 0 : items.size();
    }
}