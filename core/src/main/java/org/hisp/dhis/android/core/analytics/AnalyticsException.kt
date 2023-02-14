/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.analytics

import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.program.ProgramIndicator

sealed class AnalyticsException(message: String) : Throwable(message) {
    class InvalidArguments(message: String) : AnalyticsException(message)
    class InvalidVisualization(val uid: String) : AnalyticsException("Missing Visualization $uid")
    class InvalidDataElement(val uid: String) : AnalyticsException("Missing DataElement $uid")
    class InvalidDataElementOperand(val uid: String) : AnalyticsException("Missing DataElementOperand $uid")
    class InvalidProgramIndicator(val uid: String) : AnalyticsException("Missing ProgramIndicator $uid")
    class InvalidProgram(val uid: String) : AnalyticsException("Missing Program $uid")
    class InvalidIndicator(val uid: String) : AnalyticsException("Missing Indicator $uid")
    class InvalidOrganisationUnit(val uid: String) : AnalyticsException("Missing organisation unit $uid")
    class InvalidOrganisationUnitGroup(val uid: String) : AnalyticsException("Missing organisation unit group $uid")
    class InvalidOrganisationUnitLevel(val id: String) : AnalyticsException("Missing organisation unit level $id")
    class InvalidCategory(val uid: String) : AnalyticsException("Missing category $uid")
    class InvalidCategoryOption(val uid: String) : AnalyticsException("Missing category option $uid")
    class InvalidTrackedEntityAttribute(val uid: String) : AnalyticsException("Missing tracked entity attribute $uid")
    class UnsupportedAggregationType(val aggregationType: AggregationType) :
        AnalyticsException("Unsupported aggregation type ${aggregationType.name}")

    @Deprecated("Boundaries are supported since version 1.7.0. This exception is not thrown anymore.")
    class ProgramIndicatorCustomBoundaries(val programIndicator: ProgramIndicator) :
        AnalyticsException(
            "Custom boundaries are not supported for program indicators: " +
                "${programIndicator.displayName()} (${programIndicator.uid()})"
        )

    class SQLException(message: String) : AnalyticsException(message)
    class ParserException(message: String) : AnalyticsException(message)
}
