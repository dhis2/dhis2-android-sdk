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

package org.hisp.dhis.client.sdk.core.interpretation;

import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

public class InterpretationElementServiceImpl implements InterpretationElementService {

    public InterpretationElementServiceImpl() {
        // empty constructor
    }

    /**
     * Factory method which allows to create InterpretationElement
     * by using DashboardElement as main source of data.
     *
     * @param interpretation   Interpretation to which we will assign interpretation element
     * @param dashboardElement DashboardElement from which we want to create interpretation element.
     * @return new InterpretationElement
     */
    @Override
    public InterpretationElement create(Interpretation interpretation, DashboardElement
            dashboardElement,
                                        String mimeType) {
        Preconditions.isNull(interpretation, "interpretation must not be null");
        Preconditions.isNull(dashboardElement, "dashboardElement must not be null");
        Preconditions.isNull(mimeType, "mimeType must not be null");

        switch (mimeType) {
            case DashboardContent.TYPE_CHART:
            case DashboardContent.TYPE_MAP:
            case DashboardContent.TYPE_REPORT_TABLE:
                break;
            default:
                throw new IllegalArgumentException(mimeType + " is unsupported by interpretations" +
                        ".");
        }

        InterpretationElement interpretationElement = new InterpretationElement();
        interpretationElement.setUId(dashboardElement.getUId());
        interpretationElement.setName(dashboardElement.getName());
        interpretationElement.setDisplayName(dashboardElement.getDisplayName());
        interpretationElement.setCreated(dashboardElement.getCreated());
        interpretationElement.setLastUpdated(dashboardElement.getLastUpdated());
        interpretationElement.setAccess(dashboardElement.getAccess());
        interpretationElement.setType(mimeType);
        interpretationElement.setInterpretation(interpretation);
        return interpretationElement;
    }
}