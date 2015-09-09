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

import org.hisp.dhis.android.sdk.models.common.meta.State;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardElementService implements IDashboardElementService {
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemService dashboardItemService;

    public DashboardElementService(IDashboardElementStore dashboardElementStore,
                                   IDashboardItemService dashboardItemService) {
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemService = dashboardItemService;
    }

    /**
     * Factory method for creating DashboardElement.
     *
     * @param dashboardItem    DashboardItem to associate with element.
     * @param dashboardItemContent Content from which element will be created.
     * @return new element.
     *
     * @throws IllegalArgumentException when dashboardItem or dashboardItemContent is null.
     */
    @Override
    public DashboardElement createDashboardElement(DashboardItem dashboardItem,
                                                   DashboardItemContent dashboardItemContent) {
        isNull(dashboardItem, "dashboardItem must not be null");
        isNull(dashboardItemContent, "dashboardItemContent must not be null");

        DashboardElement element = new DashboardElement();
        element.setUId(dashboardItemContent.getUId());
        element.setName(dashboardItemContent.getName());
        element.setDisplayName(dashboardItemContent.getDisplayName());
        element.setCreated(dashboardItemContent.getCreated());
        element.setLastUpdated(dashboardItemContent.getLastUpdated());
        element.setState(State.TO_POST);
        element.setDashboardItem(dashboardItem);

        return element;
    }

    @Override
    public void deleteDashboardElement(DashboardElement dashboardElement) {
        isNull(dashboardElement, "dashboardElement must not be null");

        dashboardElement.setState(State.TO_DELETE);
        if (State.TO_POST.equals(dashboardElement.getState())) {
            dashboardElementStore.delete(dashboardElement);
        } else {
            dashboardElementStore.update(dashboardElement);
        }

        /* if count of elements in item is zero, it means
        we don't need this item anymore */
        if (!(dashboardItemService.getContentCount(dashboardElement.getDashboardItem()) > 0)) {
            dashboardItemService.deleteDashboardItem(dashboardElement.getDashboardItem());
        }
    }
}
