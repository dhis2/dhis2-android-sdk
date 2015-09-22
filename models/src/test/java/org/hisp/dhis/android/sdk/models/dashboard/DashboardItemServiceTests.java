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

import org.hisp.dhis.android.sdk.models.common.meta.Action;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DashboardItemServiceTests {
    private IDashboardItemService service;

    @Before
    public void setUp() {
        service = new DashboardItemService(
                mock(IDashboardItemStore.class),
                mock(IDashboardElementStore.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDashboardItemShouldFailOnNullDashboard() {
        DashboardItemContent content = mock(DashboardItemContent.class);
        service.createDashboardItem(null, content);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDashboardItemShouldFailOnNullContent() {
        Dashboard dashboard = mock(Dashboard.class);
        service.createDashboardItem(dashboard, null);
    }

    @Test
    public void createDashboardItemShouldCreateValidObject() {
        Dashboard dashboard = mock(Dashboard.class);
        DashboardItemContent content = mock(DashboardItemContent.class);
        when(content.getType()).thenReturn(DashboardItemContent.TYPE_MAP);

        DashboardItem item = service.createDashboardItem(dashboard, content);

        assertNotNull(item.getCreated());
        assertNotNull(item.getLastUpdated());
        assertEquals(item.getCreated(), item.getLastUpdated());

        assertEquals(item.getDashboard(), dashboard);
        assertEquals(item.getAction(), Action.TO_POST);
        assertEquals(item.getType(), DashboardItemContent.TYPE_MAP);

        assertNotNull(item.getAccess());
    }

    @Test
    public void deleteDashboardItemShouldChangeState() {
        DashboardItem dashboardItemWithPostState = new DashboardItem();
        DashboardItem dashboardItemWithSyncedState = new DashboardItem();

        dashboardItemWithPostState.setAction(Action.TO_POST);
        dashboardItemWithSyncedState.setAction(Action.SYNCED);

        service.deleteDashboardItem(dashboardItemWithPostState);
        service.deleteDashboardItem(dashboardItemWithSyncedState);

        assertEquals(dashboardItemWithPostState.getAction(), Action.TO_DELETE);
        assertEquals(dashboardItemWithSyncedState.getAction(), Action.TO_DELETE);
    }
}
