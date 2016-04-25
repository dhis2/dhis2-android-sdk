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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.meta.State;

@Table(databaseName = Dhis2Database.NAME)
public final class DashboardElement extends BaseMetaDataObject {
    static final String DASHBOARD_ITEM_KEY = "dashboardItem";

    @JsonIgnore
    @Column
    @NotNull
    State state;

    @JsonIgnore
    @Column
    @NotNull
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = DASHBOARD_ITEM_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    DashboardItem dashboardItem;

    public DashboardElement() {
        state = State.SYNCED;
    }

    /**
     * Factory method for creating DashboardElement.
     *
     * @param item    DashboardItem to associate with element.
     * @param content Content from which element will be created.
     * @return new element.
     */
    @JsonIgnore
    public static DashboardElement createDashboardElement(DashboardItem item,
                                                          DashboardItemContent content) {
        DashboardElement element = new DashboardElement();
        element.setUid(content.getUid());
        element.setName(content.getName());
        element.setCreated(content.getCreated());
        element.setLastUpdated(content.getLastUpdated());
        //element.setDisplayName(content.getDisplayName());
        element.setState(State.TO_POST);
        element.setDashboardItem(item);

        return element;
    }

    @JsonIgnore
    public void deleteDashboardElement() {
        if (State.TO_POST.equals(getState())) {
            super.delete();
        } else {
            setState(State.TO_DELETE);
            super.save();
        }

        /* if count of elements in item is zero, it means
        we don't need this item anymore */
        if (!(dashboardItem.getContentCount() > 0)) {
            dashboardItem.deleteDashboardItem();
        }
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public DashboardItem getDashboardItem() {
        return dashboardItem;
    }

    @JsonIgnore
    public void setDashboardItem(DashboardItem dashboardItem) {
        this.dashboardItem = dashboardItem;
    }
}
