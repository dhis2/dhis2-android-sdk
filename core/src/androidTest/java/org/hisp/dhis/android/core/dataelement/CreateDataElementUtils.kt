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
package org.hisp.dhis.android.core.dataelement

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType

object CreateDataElementUtils {
    private const val ID = 2L
    private const val CODE = "test_code"
    private const val NAME = "test_name"
    private const val DISPLAY_NAME = "test_display_name"
    private const val SHORT_NAME = "test_short_name"
    private const val DISPLAY_SHORT_NAME = "test_display_short_name"
    private const val DESCRIPTION = "test_description"
    private const val DISPLAY_DESCRIPTION = "test_display_description"
    private val VALUE_TYPE = ValueType.TEXT
    private const val ZERO_IS_SIGNIFICANT = false
    private const val AGGREGATION_OPERATOR = "test_aggregationOperator"
    private const val FORM_NAME = "test_formName"
    private const val DOMAIN_TYPE = "test_domainType"
    private const val DISPLAY_FORM_NAME = "test_displayFormName"

    // timestamp
    private const val DATE = "2014-03-20T13:37:00.007"

    fun create(uid: String, categoryComboId: String, optionSetId: String?): DataElement {
        return DataElement.builder()
            .id(ID)
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
            .valueType(VALUE_TYPE)
            .zeroIsSignificant(ZERO_IS_SIGNIFICANT)
            .aggregationType(AGGREGATION_OPERATOR)
            .formName(FORM_NAME)
            .domainType(DOMAIN_TYPE)
            .displayFormName(DISPLAY_FORM_NAME)
            .categoryCombo(ObjectWithUid.create(categoryComboId))
            .optionSet(ObjectWithUid.create(optionSetId))
            .build()
    }
}
