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
package org.hisp.dhis.android.core.common

import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import java.text.ParseException
import java.util.Date

interface BaseIdentifiableObject : IdentifiableObject, ObjectWithDeleteInterface {
    override fun uid(): String

    override fun code(): String?

    override fun name(): String?

    override fun displayName(): String?

    override fun created(): Date?

    override fun lastUpdated(): Date?

    override fun deleted(): Boolean?

    interface Builder<T : Builder<T>> {
        fun uid(uid: String?): T

        fun code(code: String?): T

        fun name(name: String?): T

        fun displayName(displayName: String?): T

        fun created(created: Date?): T

        @Throws(ParseException::class)
        fun created(createdStr: String): T {
            return created(DATE_FORMAT.parse(createdStr))
        }

        fun lastUpdated(lastUpdated: Date?): T

        @Throws(ParseException::class)
        fun lastUpdated(lastUpdatedStr: String): T {
            return lastUpdated(DATE_FORMAT.parse(lastUpdatedStr))
        }

        fun deleted(deleted: Boolean?): T
    }

    @Suppress("MayBeConst")
    companion object {
        /* date format which should be used for all Date instances
    within models which extend BaseIdentifiableObject */
        @JvmField
        val DATE_FORMAT: SafeDateFormat = DateUtils.DATE_FORMAT

        @JvmField
        val SPACE_DATE_FORMAT: SafeDateFormat = DateUtils.SPACE_DATE_FORMAT

        @JvmField val UID: String = "id"

        @JvmField val UUID: String = "uid"

        @JvmField val CODE: String = "code"

        @JvmField val NAME: String = "name"

        @JvmField val DISPLAY_NAME: String = "displayName"

        @JvmField val CREATED: String = "created"

        @JvmField val LAST_UPDATED: String = "lastUpdated"

        @JvmField val DELETED: String = "deleted"

        @Throws(ParseException::class)
        @JvmStatic
        fun parseDate(dateStr: String): Date {
            return DATE_FORMAT.parse(dateStr)
        }

        @Throws(ParseException::class)
        @JvmStatic
        fun parseSpaceDate(dateStr: String): Date {
            return SPACE_DATE_FORMAT.parse(dateStr)
        }

        @JvmStatic
        fun dateToSpaceDateStr(date: Date): String {
            return SPACE_DATE_FORMAT.format(date)
        }

        @JvmStatic
        fun dateToDateStr(date: Date): String {
            return DATE_FORMAT.format(date)
        }
    }
}
