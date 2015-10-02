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

import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.state.IStateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class DashboardElementService implements IDashboardElementService {
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemService dashboardItemService;
    private final IStateStore stateStore;

    public DashboardElementService(IDashboardElementStore dashboardElementStore,
                                   IDashboardItemService dashboardItemService,
                                   IStateStore stateStore) {
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemService = dashboardItemService;
        this.stateStore = stateStore;
    }

    /**
     * Factory method for creating DashboardElement.
     *
     * @param dashboardItem        DashboardItem to associate with element.
     * @param dashboardContent Content from which element will be created.
     * @return new element.
     * @throws IllegalArgumentException when dashboardItem or dashboardContent is null.
     */
    @Override
    public DashboardElement add(DashboardItem dashboardItem, DashboardContent dashboardContent) {
        isNull(dashboardItem, "dashboardItem must not be null");
        isNull(dashboardContent, "dashboardContent must not be null");

        DashboardElement element = new DashboardElement();
        element.setUId(dashboardContent.getUId());
        element.setName(dashboardContent.getName());
        element.setDisplayName(dashboardContent.getDisplayName());
        element.setCreated(dashboardContent.getCreated());
        element.setLastUpdated(dashboardContent.getLastUpdated());
        element.setDashboardItem(dashboardItem);

        // element.setAction(Action.TO_POST);
        stateStore.save(element, Action.TO_POST);

        return element;
    }

    @Override
    public void remove(DashboardElement dashboardElement) {
        isNull(dashboardElement, "dashboardElement must not be null");

        Action action = stateStore.queryAction(dashboardElement);
        if (Action.TO_POST.equals(action)) {
            // dashboardElement.setAction(Action.TO_DELETE);
            stateStore.delete(dashboardElement);
            dashboardElementStore.delete(dashboardElement);
        } else {
            // dashboardElement.setAction(Action.TO_DELETE);
            stateStore.save(dashboardElement, Action.TO_DELETE);
            dashboardElementStore.update(dashboardElement);
        }

        /* if count of elements in item is zero, it means
        we don't need this item anymore */
        if (!(dashboardItemService.getContentCount(dashboardElement.getDashboardItem()) > 0)) {
            dashboardItemService.remove(dashboardElement.getDashboardItem());
        }
    }

    @Override
    public List<DashboardElement> list(DashboardItem dashboardItem) {
        List<DashboardElement> allDashboardElements = dashboardElementStore.queryByDashboardItem(dashboardItem);
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardElement.class);

        List<DashboardElement> dashboardElements = new ArrayList<>();
        for (DashboardElement dashboardElement : allDashboardElements) {
            Action action = actionMap.get(dashboardElement.getId());

            if (!Action.TO_DELETE.equals(action)) {
                dashboardElements.add(dashboardElement);
            }
        }

        return dashboardElements;
    }

    @Override
    public List<DashboardElement> list() {
        List<DashboardElement> elements = dashboardElementStore.queryAll();
        Map<Long, Action> actionMap = stateStore.queryMap(DashboardElement.class);

        List<DashboardElement> dashboardElements = new ArrayList<>();
        for (DashboardElement dashboardElement : elements) {
            Action action = actionMap.get(dashboardElement.getId());

            if (!Action.TO_DELETE.equals(action)) {
                dashboardElements.add(dashboardElement);
            }
        }

        return dashboardElements;
    }
}
