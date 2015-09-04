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

package org.hisp.dhis.android.sdk.models.providers;

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardService;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardStore;
import org.joda.time.DateTime;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Matchers.anyString;

public final class DashboardMockProvider {
    private static final Random random = new Random();

    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    private final IDashboardService dashboardService;
    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;

    public DashboardMockProvider() {
        dashboardStore = Mockito.mock(IDashboardStore.class);
        dashboardItemStore = Mockito.mock(IDashboardItemStore.class);
        dashboardElementStore = Mockito.mock(IDashboardElementStore.class);

        dashboardService = Mockito.mock(IDashboardService.class);
        dashboardItemService = Mockito.mock(IDashboardItemService.class);
        dashboardElementService = Mockito.mock(IDashboardElementService.class);
    }

    public IDashboardElementService getDashboardElementService() {
        return dashboardElementService;
    }

    public IDashboardItemService getDashboardItemService() {
        return dashboardItemService;
    }

    public IDashboardService getDashboardService() {
        return dashboardService;
    }

    public IDashboardStore provideDashboardStore() {
        return dashboardStore;
    }

    public IDashboardItemStore provideDashboardItemStore() {
        return dashboardItemStore;
    }

    public IDashboardElementStore provideDashboardElementStore() {
        return dashboardElementStore;
    }

    public static List<DashboardElement> provideFakeDashboardElements() {
        List<DashboardElement> dashboardElements = new ArrayList<>();

        return dashboardElements;
    }

    /* public Dashboard mock(State state) {
        final String name = anyString();
        final DateTime lastUpdated = new DateTime();
        final Access access = Access.createDefaultAccess();

        Dashboard dashboard = new Dashboard();
        dashboard.setId(random.nextInt());
        dashboard.setUId(UUID.randomUUID().toString());
        dashboard.setName(name);
        dashboard.setDisplayName(name);
        dashboard.setCreated(lastUpdated);
        dashboard.setLastUpdated(lastUpdated);
        dashboard.setAccess(access);
        dashboard.setState(state);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        for (int i = 0; i < random.nextInt(32); i++) {
            dashboardItems.add(mock(dashboard));
        }

        dashboard.setDashboardItems(dashboardItems);
        return dashboard;
    }

    public DashboardItem mock(Dashboard dashboard) {
        final String NAME = anyString();
        final DateTime dateTime = new DateTime();
        final Access access = new Access();

        DashboardItem dashboardItem = new DashboardItem();
        dashboardItem.setId(random.nextInt());
        dashboardItem.setUId(UUID.randomUUID().toString());
        dashboardItem.setName(NAME);
        dashboardItem.setDisplayName(NAME);
        dashboardItem.setCreated(dateTime);
        dashboardItem.setLastUpdated(dateTime);
        dashboardItem.setAccess(access);
        dashboardItem.setDashboard(dashboard);

        return dashboardItem;
    } */

    public static DashboardElement provideDashboardElement() {
        return null;
    }
}
