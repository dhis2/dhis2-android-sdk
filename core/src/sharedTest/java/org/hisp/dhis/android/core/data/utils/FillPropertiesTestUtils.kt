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
package org.hisp.dhis.android.core.data.utils

import java.text.ParseException
import java.util.*
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseNameableObject

/**
 * A collection of convenience functions/abstractions to be used by the tests.
 */
object FillPropertiesTestUtils {
    const val UID = "test_uid"
    const val CODE = "test_code"
    const val NAME = "test_name"
    const val DISPLAY_NAME = "test_display_name"
    private const val CREATED_STR = "2012-10-20T18:20:27.132"
    @JvmField
    val CREATED = parseDate(CREATED_STR)
    const val LAST_UPDATED_STR = "2017-12-20T15:08:27.882"
    @JvmField
    val LAST_UPDATED = parseDate(LAST_UPDATED_STR)
    private const val SHORT_NAME = "test_short_name"
    private const val DISPLAY_SHORT_NAME = "test_display_short_name"
    private const val DESCRIPTION = "test_description"
    private const val DISPLAY_DESCRIPTION = "test_display_description"
    const val TABLE = "test_table"
    private const val FUTURE_DATE_STR = "3000-12-20T15:08:27.882"
    @JvmField
    val FUTURE_DATE = parseDate(FUTURE_DATE_STR)

    @JvmStatic
    fun parseDate(dateStr: String?): Date {
        return try {
            BaseIdentifiableObject.DATE_FORMAT.parse(dateStr!!)
        } catch (e: ParseException) {
            Date()
        }
    }

    @JvmStatic
    fun fillIdentifiableProperties(builder: BaseIdentifiableObject.Builder<*>) {
        builder
            .uid(UID)
            .code(CODE)
            .name(NAME)
            .displayName(DISPLAY_NAME)
            .created(CREATED)
            .lastUpdated(LAST_UPDATED)
    }

    @JvmStatic
    fun fillNameableProperties(builder: BaseNameableObject.Builder<*>) {
        fillIdentifiableProperties(builder)
        builder
            .shortName(SHORT_NAME)
            .displayShortName(DISPLAY_SHORT_NAME)
            .description(DESCRIPTION)
            .displayDescription(DISPLAY_DESCRIPTION)
    }
}
