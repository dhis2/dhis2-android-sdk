/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType

object CreateTrackedEntityAttributeUtils {
    /**
     * BaseIdentifiable properties
     */
    private const val CODE = "test_code"
    private const val NAME = "test_name"
    private const val DISPLAY_NAME = "test_display_name"
    private const val DATE = "2011-12-24T12:24:25.203"

    /**
     * BaseNameableProperties
     */
    private const val SHORT_NAME = "test_short_name"
    private const val DISPLAY_SHORT_NAME = "test_display_short_name"
    private const val DESCRIPTION = "test_description"
    private const val DISPLAY_DESCRIPTION = "test_display_description"

    /**
     * Properties bound to TrackedEntityAttribute
     */
    private const val PATTERN = "test_pattern"
    private const val SORT_ORDER_IN_LIST_NO_PROGRAM = 1
    private val VALUE_TYPE = ValueType.BOOLEAN
    private const val EXPRESSION = "test_expression"
    private const val PROGRAM_SCOPE = false
    private const val DISPLAY_IN_LIST_NO_PROGRAM = true
    private const val GENERATED = false
    private const val DISPLAY_ON_VISIT_SCHEDULE = true
    private const val ORG_UNIT_SCOPE = false
    private const val UNIQUE = true
    private const val INHERIT = false
    private const val CONFIDENTIAL = false

    fun create(uid: String?, optionSetUid: String?): TrackedEntityAttribute {
        return TrackedEntityAttribute.builder()
            .uid(uid)
            .code(CODE)
            .name(NAME)
            .displayName(DISPLAY_NAME)
            .created(DATE)
            .lastUpdated(DATE)
            .shortName(SHORT_NAME)
            .displayShortName(DISPLAY_SHORT_NAME)
            .description(DESCRIPTION)
            .displayDescription(DISPLAY_DESCRIPTION)
            .pattern(PATTERN)
            .sortOrderInListNoProgram(SORT_ORDER_IN_LIST_NO_PROGRAM)
            .valueType(VALUE_TYPE)
            .expression(EXPRESSION)
            .programScope(PROGRAM_SCOPE)
            .displayInListNoProgram(DISPLAY_IN_LIST_NO_PROGRAM)
            .generated(GENERATED)
            .displayOnVisitSchedule(DISPLAY_ON_VISIT_SCHEDULE)
            .orgUnitScope(ORG_UNIT_SCOPE)
            .unique(UNIQUE)
            .inherit(INHERIT)
            .confidential(CONFIDENTIAL)
            .optionSet(ObjectWithUid.create(optionSetUid))
            .build()
    }
}
