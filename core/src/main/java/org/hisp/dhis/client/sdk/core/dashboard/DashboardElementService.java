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

import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardElementService implements IDashboardElementService {
    private final IStateStore stateStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemService dashboardItemService;

    public DashboardElementService(IStateStore stateStore, IDashboardElementStore elementStore,
                                   IDashboardItemService dashboardItemService) {
        this.stateStore = stateStore;
        this.dashboardElementStore = elementStore;
        this.dashboardItemService = dashboardItemService;
    }

    @Override
    public DashboardElement create(DashboardItem dashboardItem, DashboardContent dashboardContent) {
        Preconditions.isNull(dashboardItem, "DashboardItem object must not be null");
        Preconditions.isNull(dashboardContent, "DashboardContent object must not be null");

        String uid = CodeGenerator.generateCode();
        Access access = Access.createDefaultAccess();

        DashboardElement dashboardElement = new DashboardElement();
        dashboardElement.setUId(uid);
        dashboardElement.setName(dashboardContent.getName());
        dashboardElement.setDisplayName(dashboardContent.getDisplayName());
        dashboardElement.setCreated(dashboardContent.getCreated());
        dashboardElement.setLastUpdated(dashboardContent.getLastUpdated());
        dashboardElement.setAccess(access);
        dashboardElement.setDashboardItem(dashboardItem);

        return dashboardElement;
    }

    @Override
    public boolean remove(DashboardElement object) {
        Preconditions.isNull(object, "DashboardElement object must not be null");

        Action action = stateStore.queryActionForModel(object);
        boolean isRemoved = false;
        if (action != null) {
            switch (action) {
                case SYNCED:
                case TO_UPDATE: {
                    /* for SYNCED and TO_UPDATE states we need only to mark model as removed */
                    isRemoved = stateStore.saveActionForModel(object, Action.TO_DELETE);
                    break;
                }
                case TO_POST: {
                    isRemoved = dashboardElementStore.delete(object);
                    break;
                }
                case TO_DELETE: {
                    isRemoved = false;
                    break;
                }
            }
        }

        if (isRemoved && !(dashboardItemService.countElements(object.getDashboardItem()) > 1)) {
            isRemoved = dashboardItemService.remove(object.getDashboardItem());
        }

        return isRemoved;
    }

    @Override
    public List<DashboardElement> list() {
        return stateStore.queryModelsWithActions(DashboardElement.class,
                Action.SYNCED, Action.TO_POST, Action.TO_UPDATE);
    }

    @Override
    public List<DashboardElement> list(DashboardItem dashboardItem) {
        Preconditions.isNull(dashboardItem, "DashboardItem object must not be null");

        List<DashboardElement> allDashboardElements = dashboardElementStore.queryByDashboardItem(dashboardItem);
        Map<Long, Action> actionMap = stateStore.queryActionsForModel(DashboardElement.class);

        List<DashboardElement> dashboardElements = new ArrayList<>();
        for (DashboardElement dashboardElement : allDashboardElements) {
            Action action = actionMap.get(dashboardElement.getId());

            if (!Action.TO_DELETE.equals(action)) {
                dashboardElements.add(dashboardElement);
            }
        }

        return dashboardElements;
    }
}
