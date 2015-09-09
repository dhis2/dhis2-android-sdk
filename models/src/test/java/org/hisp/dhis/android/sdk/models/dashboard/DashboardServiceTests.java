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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DashboardServiceTests {

    /* convenience static variable for setting dashboard name */
    private final static String DASHBOARD_NAME = "randomDashboardName";

    private IDashboardService service;

    @Before
    public void setUp() {
        service = new DashboardService(
                mock(IDashboardStore.class),
                mock(IDashboardItemStore.class),
                mock(IDashboardElementStore.class),
                mock(IDashboardItemService.class),
                mock(IDashboardElementService.class));
    }

    @Test
    public void shouldCreateValidDashboard() {
        Dashboard dashboard = service.createDashboard(DASHBOARD_NAME);

        assertEquals(dashboard.getName(), DASHBOARD_NAME);
        assertEquals(dashboard.getDisplayName(), DASHBOARD_NAME);
        assertEquals(dashboard.getState(), State.TO_POST);

        assertNotNull(dashboard.getAccess());
        assertNotNull(dashboard.getCreated());
        assertNotNull(dashboard.getLastUpdated());

        assertTrue(dashboard.getCreated()
                .equals(dashboard.getLastUpdated()));
    }

    @Test
    public void shouldUpdateSyncedDashboardName() {
        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.SYNCED);

        service.updateDashboardName(dashboard, DASHBOARD_NAME);

        assertEquals(dashboard.getName(), DASHBOARD_NAME);
        assertEquals(dashboard.getDisplayName(), DASHBOARD_NAME);
        assertEquals(dashboard.getState(), State.TO_UPDATE);
    }

    @Test
    public void shouldUpdatePostDashboardName() {
        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_POST);

        service.updateDashboardName(dashboard, DASHBOARD_NAME);

        assertEquals(dashboard.getName(), DASHBOARD_NAME);
        assertEquals(dashboard.getDisplayName(), DASHBOARD_NAME);
        assertEquals(dashboard.getState(), State.TO_POST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullDashboard() {
        service.updateDashboardName(null, DASHBOARD_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDashboardNameWithDeleteState() {
        Dashboard dashboard = new Dashboard();
        dashboard.setState(State.TO_DELETE);

        service.updateDashboardName(dashboard, DASHBOARD_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullDashboard() {
        service.deleteDashboard(null);
    }

    @Test
    public void deleteDashboard() {
        Dashboard dashboardSynced = new Dashboard();
        Dashboard dashboardUpdated = new Dashboard();
        Dashboard dashboardPost = new Dashboard();

        dashboardSynced.setState(State.SYNCED);
        dashboardUpdated.setState(State.TO_UPDATE);
        dashboardPost.setState(State.TO_POST);

        service.deleteDashboard(dashboardSynced);
        service.deleteDashboard(dashboardUpdated);
        service.deleteDashboard(dashboardPost);

        assertEquals(dashboardSynced.getState(), State.TO_DELETE);
        assertEquals(dashboardUpdated.getState(), State.TO_DELETE);
        assertEquals(dashboardPost.getState(), State.TO_DELETE);
    }

    // TODO finish this test after others for element and item services.
    @Test
    public void shouldAddContentToDashboard() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void addContentToNullDashboard() {
        DashboardItemContent content =
                mock(DashboardItemContent.class);
        service.addDashboardContent(null, content);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullContentToDashboard() {
        Dashboard dashboard = mock(Dashboard.class);
        service.addDashboardContent(dashboard, null);
    }

    @Test
    public void getAvailableItemByTypeShouldReturnNull() {
        DashboardItem dashboardItemOne = mock(DashboardItem.class);
        when(dashboardItemOne.getType()).thenReturn(DashboardItemContent.TYPE_CHART);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        dashboardItems.add(dashboardItemOne);

        /* creating stub service which returns maximum count of items every time */
        IDashboardItemService dashboardItemService = mock(IDashboardItemService.class);
        when(dashboardItemService.getContentCount(any(DashboardItem.class)))
                .thenReturn(DashboardItem.MAX_CONTENT);

        IDashboardItemStore dashboardItemStore = mock(IDashboardItemStore.class);
        when(dashboardItemStore.filter(any(Dashboard.class),
                any(State.class))).thenReturn(dashboardItems);

        IDashboardService dashboardService = new DashboardService(
                mock(IDashboardStore.class), dashboardItemStore,
                mock(IDashboardElementStore.class), dashboardItemService,
                mock(IDashboardElementService.class));

        assertNull(dashboardService.getAvailableItemByType(
                mock(Dashboard.class), DashboardItemContent.TYPE_CHART));
    }

    @Test
    public void getAvailableItemByTypeShouldReturnNullBasedOnWrongType() {
        DashboardItem dashboardItemOne = mock(DashboardItem.class);
        when(dashboardItemOne.getType()).thenReturn(DashboardItemContent.TYPE_MAP);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        dashboardItems.add(dashboardItemOne);

        /* creating stub service which returns maximum count of items every time */
        IDashboardItemService dashboardItemService = mock(IDashboardItemService.class);
        when(dashboardItemService.getContentCount(any(DashboardItem.class)))
                .thenReturn(DashboardItem.MAX_CONTENT - 16);

        IDashboardItemStore dashboardItemStore = mock(IDashboardItemStore.class);
        when(dashboardItemStore.filter(any(Dashboard.class),
                any(State.class))).thenReturn(dashboardItems);

        IDashboardService dashboardService = new DashboardService(
                mock(IDashboardStore.class), dashboardItemStore,
                mock(IDashboardElementStore.class), dashboardItemService,
                mock(IDashboardElementService.class));

        assertNull(dashboardService.getAvailableItemByType(
                mock(Dashboard.class), DashboardItemContent.TYPE_CHART));
    }

    @Test
    public void getAvailableItemByTypeShouldReturnItem() {
        DashboardItem dashboardItemOne = mock(DashboardItem.class);
        when(dashboardItemOne.getType()).thenReturn(DashboardItemContent.TYPE_MAP);

        List<DashboardItem> dashboardItems = new ArrayList<>();
        dashboardItems.add(dashboardItemOne);

        /* creating stub service which returns maximum count of items every time */
        IDashboardItemService dashboardItemService = mock(IDashboardItemService.class);
        when(dashboardItemService.getContentCount(any(DashboardItem.class)))
                .thenReturn(DashboardItem.MAX_CONTENT - 16);

        IDashboardItemStore dashboardItemStore = mock(IDashboardItemStore.class);
        when(dashboardItemStore.filter(any(Dashboard.class),
                any(State.class))).thenReturn(dashboardItems);

        IDashboardService dashboardService = new DashboardService(
                mock(IDashboardStore.class), dashboardItemStore,
                mock(IDashboardElementStore.class), dashboardItemService,
                mock(IDashboardElementService.class));

        assertEquals(dashboardService.getAvailableItemByType(
                mock(Dashboard.class), DashboardItemContent.TYPE_MAP), dashboardItemOne);
    }
}
