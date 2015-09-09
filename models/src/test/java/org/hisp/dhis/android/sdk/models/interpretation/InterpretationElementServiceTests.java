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

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItemContent;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public final class InterpretationElementServiceTests {
    private IInterpretationElementService service;

    @Before
    public void setUp() {
        service = new InterpretationElementService();
    }

    @Test
    public void createInterpretationElementShouldCreateValidObject() {
        DateTime lastUpdated = new DateTime();

        Interpretation interpretation = mock(Interpretation.class);
        String mimeType = DashboardItemContent.TYPE_CHART;

        DashboardElement dashboardElement = new DashboardElement();
        dashboardElement.setUId("df923hasf");
        dashboardElement.setName("SomeFancyName");
        dashboardElement.setDisplayName("SomeFancyName");
        dashboardElement.setCreated(lastUpdated);
        dashboardElement.setLastUpdated(lastUpdated);
        dashboardElement.setAccess(Access.createDefaultAccess());

        InterpretationElement interpretationElement = service
                .createInterpretationElement(interpretation, dashboardElement, mimeType);
        assertEquals(interpretationElement.getUId(), dashboardElement.getUId());
        assertEquals(interpretationElement.getName(), dashboardElement.getName());
        assertEquals(interpretationElement.getDisplayName(), dashboardElement.getDisplayName());
        assertEquals(interpretationElement.getCreated(), dashboardElement.getCreated());
        assertEquals(interpretationElement.getLastUpdated(), dashboardElement.getLastUpdated());
        assertEquals(interpretationElement.getInterpretation(), interpretation);
        assertEquals(interpretationElement.getType(), mimeType);

        assertNotNull(interpretationElement.getAccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInterpretationElementShouldFailOnNullInterpretation() {
        DashboardElement dashboardElement = mock(DashboardElement.class);
        String mimeType = DashboardItemContent.TYPE_CHART;
        service.createInterpretationElement(null, dashboardElement, mimeType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInterpretationElementShouldFailOnNullDashboardElement() {
        Interpretation interpretation = new Interpretation();
        String mimeType = DashboardItemContent.TYPE_CHART;
        service.createInterpretationElement(interpretation, null, mimeType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInterpretationElementShouldFailOnNullMimeType() {
        Interpretation interpretation = mock(Interpretation.class);
        DashboardElement dashboardElement = mock(DashboardElement.class);
        service.createInterpretationElement(interpretation, dashboardElement, null);
    }

    @Test
    public void createInterpretationElementShouldFailOnWrongType() {
        /* should now throw exception */
        assertFalse(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_CHART));
        assertFalse(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_MAP));
        assertFalse(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_REPORT_TABLE));

        /* should throw exception */
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_EVENT_CHART));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_EVENT_REPORT));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_USERS));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_REPORTS));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_RESOURCES));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_REPORT_TABLES));
        assertTrue(createInterpretationElementShouldFailOnWrongType(DashboardItemContent.TYPE_MESSAGES));
    }

    /* returns true if exception was caught */
    private boolean createInterpretationElementShouldFailOnWrongType(String type) {
        try {
            Interpretation interpretation = mock(Interpretation.class);
            DashboardElement dashboardElement = mock(DashboardElement.class);
            service.createInterpretationElement(interpretation, dashboardElement, type);
        } catch(IllegalArgumentException exception) {
            return true;
        }

        return false;
    }
}
