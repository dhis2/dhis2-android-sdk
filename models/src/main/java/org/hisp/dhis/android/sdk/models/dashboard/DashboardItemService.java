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

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.joda.time.DateTime;

import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardItemService implements IDashboardItemService {
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    public DashboardItemService(IDashboardItemStore dashboardItemStore,
                                IDashboardElementStore dashboardElementStore) {
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
    }

    /**
     * Factory method which creates and returns DashboardItem.
     *
     * @param dashboard Dashboard to associate with item.
     * @param content   Content for dashboard item.
     * @return new item.
     */
    @Override
    public DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content) {
        isNull(dashboard, "dashboard must not be null");
        isNull(content, "content must not be null");

        DateTime lastUpdated = new DateTime();

        DashboardItem item = new DashboardItem();
        item.setCreated(lastUpdated);
        item.setLastUpdated(lastUpdated);
        item.setAction(Action.TO_POST);
        item.setDashboard(dashboard);
        item.setAccess(Access.createDefaultAccess());
        item.setType(content.getType());

        return item;
    }

    /**
     * This method will change the action of the model to TO_DELETE
     * if the model was already synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from embedded database.
     */
    @Override
    public void deleteDashboardItem(DashboardItem dashboardItem) {
        if (Action.TO_POST.equals(dashboardItem.getAction())) {
            dashboardItem.setAction(Action.TO_DELETE);
            dashboardItemStore.delete(dashboardItem);
        } else {
            dashboardItem.setAction(Action.TO_DELETE);
            dashboardItemStore.update(dashboardItem);
        }
    }

    @Override
    public int getContentCount(DashboardItem dashboardItem) {
        List<DashboardElement> dashboardElements = dashboardElementStore
                .filter(dashboardItem, Action.TO_DELETE);
        return dashboardElements == null ? 0 : dashboardElements.size();
    }
}
