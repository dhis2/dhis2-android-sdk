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
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationElementService;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationService;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationStore;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationService;
import org.hisp.dhis.android.sdk.models.user.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

public final class InterpretationServiceTest {
    private final IInterpretationService service;

    public InterpretationServiceTest() {
        service = new InterpretationService(
                mock(IInterpretationStore.class),
                mock(IInterpretationElementService.class));
    }

    @Test
    public void shouldAddCommentToInterpretation() {
        Interpretation interpretation = mock(Interpretation.class);
        User user = mock(User.class);
        String text = anyString();

        InterpretationComment comment = service.addComment(
                interpretation, user, text);

        assertEquals(comment.getInterpretation(), interpretation);
        assertEquals(comment.getUser(), user);
        assertEquals(comment.getText(), text);

        /* we need to make sure that State is State.TO_POST */
        assertEquals(comment.getState(), State.TO_POST);

        assertNotNull(comment.getCreated());
        assertNotNull(comment.getLastUpdated());
        assertNotNull(comment.getAccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCommentShouldFailOnNullInterpretation() {
        User user = mock(User.class);
        String text = anyString();

        service.addComment(null, user, text);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCommentShouldFailOnNullUser() {
        Interpretation interpretation = mock(Interpretation.class);
        String text = anyString();

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
        String text = anyString();

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
        assertEquals(interpretationChart.getState(), State.TO_POST);


        assertEquals(interpretationChart.getType(), Interpretation.TYPE_CHART);
        assertNotNull(interpretationChart.getChart());

        assertEquals(interpretationMap.getType(), Interpretation.TYPE_MAP);
        assertNotNull(interpretationMap.getMap());

        assertEquals(interpretationReportTable.getType(), Interpretation.TYPE_REPORT_TABLE);
        assertNotNull(interpretationReportTable.getReportTable());
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnWrongDashboardItemType() {
        DashboardItem dashboardItem = new DashboardItem();
        User user = mock(User.class);
        String text = anyString();

        dashboardItem.setType(DashboardItemContent.TYPE_EVENT_CHART);

        Interpretation interpretation = service.createInterpretation(
                dashboardItem, user, text);

    }
}
