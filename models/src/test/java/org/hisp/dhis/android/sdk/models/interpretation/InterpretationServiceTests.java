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

package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.meta.Action;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;
import org.hisp.dhis.android.sdk.models.user.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class InterpretationServiceTests {
    private final IInterpretationService service;

    public InterpretationServiceTests() {
        InterpretationElementService elementServiceMock
                = mock(InterpretationElementService.class);
        when(elementServiceMock.createInterpretationElement(
                any(Interpretation.class), any(DashboardElement.class),
                anyString())).thenReturn(mock(InterpretationElement.class));
        service = new InterpretationService(
                mock(IInterpretationStore.class),
                elementServiceMock);
    }

    @Test
    public void shouldAddCommentToInterpretation() {
        Interpretation interpretation = mock(Interpretation.class);
        User user = mock(User.class);
        String text = "Interpretation comment";

        InterpretationComment comment = service.addComment(
                interpretation, user, text);

        assertEquals(comment.getInterpretation(), interpretation);
        assertEquals(comment.getUser(), user);
        assertEquals(comment.getText(), text);

        /* we need to make sure that Action is Action.TO_POST */
        assertEquals(comment.getAction(), Action.TO_POST);

        assertNotNull(comment.getCreated());
        assertNotNull(comment.getLastUpdated());
        assertNotNull(comment.getAccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCommentShouldFailOnNullInterpretation() {
        User user = mock(User.class);
        String text = "Interpretation comment";

        service.addComment(null, user, text);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCommentShouldFailOnNullUser() {
        Interpretation interpretation = mock(Interpretation.class);
        String text = "Interpretation comment";

        service.addComment(interpretation, null, text);
    }

    @Test
    public void shouldCreateValidInterpretations() {
        DashboardItem dashboardItemChart = new DashboardItem();
        DashboardItem dashboardItemMap = new DashboardItem();
        DashboardItem dashboardItemReportTable = new DashboardItem();

        DashboardElement dashboardElementChart = mock(DashboardElement.class);
        DashboardElement dashboardElementMap = mock(DashboardElement.class);
        DashboardElement dashboardElementReportTable = mock(DashboardElement.class);

        dashboardItemChart.setChart(dashboardElementChart);
        dashboardItemChart.setType(DashboardItemContent.TYPE_CHART);

        dashboardItemMap.setMap(dashboardElementMap);
        dashboardItemMap.setType(DashboardItemContent.TYPE_MAP);

        dashboardItemReportTable.setMap(dashboardElementReportTable);
        dashboardItemReportTable.setType(DashboardItemContent.TYPE_REPORT_TABLE);

        User user = mock(User.class);
        String text = "Interpretation text";

        Interpretation interpretationChart = service
                .createInterpretation(dashboardItemChart, user, text);
        Interpretation interpretationMap = service
                .createInterpretation(dashboardItemMap, user, text);
        Interpretation interpretationReportTable = service
                .createInterpretation(dashboardItemReportTable, user, text);

        /* testing fields common for all interpretation */
        assertNotNull(interpretationChart.getCreated());
        assertNotNull(interpretationChart.getLastUpdated());
        assertNotNull(interpretationChart.getAccess());

        assertEquals(interpretationChart.getText(), text);
        assertEquals(interpretationChart.getUser(), user);
        assertEquals(interpretationChart.getAction(), Action.TO_POST);

        assertEquals(interpretationChart.getType(), Interpretation.TYPE_CHART);
        assertNotNull(interpretationChart.getChart());

        assertEquals(interpretationMap.getType(), Interpretation.TYPE_MAP);
        assertNotNull(interpretationMap.getMap());

        assertEquals(interpretationReportTable.getType(), Interpretation.TYPE_REPORT_TABLE);
        assertNotNull(interpretationReportTable.getReportTable());
    }

    @Test
    public void shouldThrowExceptionOnWrongDashboardItemType() {
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_EVENT_CHART);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_EVENT_REPORT);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_MESSAGES);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_REPORTS);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_RESOURCES);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_USERS);
        shouldThrowExceptionOnWrongDashboardItemType(DashboardItemContent.TYPE_REPORT_TABLES);
    }

    private void shouldThrowExceptionOnWrongDashboardItemType(String type) {
        DashboardItem dashboardItem = new DashboardItem();
        dashboardItem.setType(type);

        User user = mock(User.class);
        String text = "Interpretation text";

        assertTrue(shouldThrowExceptionOnWrongDashboardItemType(dashboardItem, user, text));
    }

    private boolean shouldThrowExceptionOnWrongDashboardItemType(
            DashboardItem dashboardItem, User user, String text) {
        try {
            service.createInterpretation(dashboardItem, user, text);
        } catch (IllegalArgumentException exception) {
            return true;
        }

        return false;
    }
}
