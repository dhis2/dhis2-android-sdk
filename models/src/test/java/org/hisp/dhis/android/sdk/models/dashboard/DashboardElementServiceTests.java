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
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public final class DashboardElementServiceTests {
    private IDashboardElementService service;

    @Before
    public void setUp() {
        service = new DashboardElementService(
                mock(IDashboardElementStore.class),
                mock(IDashboardItemService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDashboardElementShouldFailOnNullItem() {
        DashboardItemContent content = mock(DashboardItemContent.class);
        service.createDashboardElement(null, content);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDashboardElementShouldFailOnNullContent() {
        DashboardItem item = mock(DashboardItem.class);
        service.createDashboardElement(item, null);
    }

    @Test
    public void createDashboardElementShouldCreateValidObject() {
        DateTime lastUpdated = new DateTime();

        DashboardItem dashboardItem = mock(DashboardItem.class);
        DashboardItemContent dashboardItemContent = new DashboardItemContent();

        dashboardItemContent.setUId("364dgfeq52");
        dashboardItemContent.setName("RandomName");
        dashboardItemContent.setDisplayName("RandomName");
        dashboardItemContent.setCreated(lastUpdated);
        dashboardItemContent.setLastUpdated(lastUpdated);

        DashboardElement dashboardElement = service.createDashboardElement(
                dashboardItem, dashboardItemContent);

        assertEquals(dashboardElement.getUId(), dashboardItemContent.getUId());
        assertEquals(dashboardElement.getName(), dashboardItemContent.getName());
        assertEquals(dashboardElement.getDisplayName(), dashboardItemContent.getDisplayName());
        assertEquals(dashboardElement.getCreated(), dashboardItemContent.getCreated());
        assertEquals(dashboardElement.getLastUpdated(), dashboardItemContent.getLastUpdated());
        assertEquals(dashboardElement.getState(), State.TO_POST);
        assertEquals(dashboardElement.getDashboardItem(), dashboardItem);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteDashboardElementShouldFailOnNullElement() {
        service.deleteDashboardElement(null);
    }

    @Test
    public void deleteDashboardElementShouldChangeTheState() {
        DashboardElement dashboardElementWithPostState = new DashboardElement();
        DashboardElement dashboardElementWithSyncedState = new DashboardElement();

        dashboardElementWithPostState.setState(State.TO_POST);
        dashboardElementWithSyncedState.setState(State.SYNCED);

        service.deleteDashboardElement(dashboardElementWithPostState);
        service.deleteDashboardElement(dashboardElementWithSyncedState);

        assertEquals(dashboardElementWithPostState.getState(), State.TO_DELETE);
        assertEquals(dashboardElementWithSyncedState.getState(), State.TO_DELETE);
    }
}
