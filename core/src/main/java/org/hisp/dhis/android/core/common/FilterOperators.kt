/*
 *  Copyright (c) 2004-2026, University of Oslo
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

@Suppress("FunctionParameterNaming", "VariableNaming")
interface FilterOperators {

    /** Less than or equal to */
    val le: String?

    /** Greater than or equal to */
    val ge: String?

    /** Greater than */
    val gt: String?

    /** Lesser than */
    val lt: String?

    /** Equal to */
    val eq: String?

    /** In a list */
    val `in`: Set<String>?

    /** Like */
    val like: String?

    /** If the dataItem is of type date, then date filtering parameters are specified using this. */
    val dateFilter: DateFilterPeriod?

    val isEmpty: Boolean?

    fun le(): String? = le
    fun ge(): String? = ge
    fun gt(): String? = gt
    fun lt(): String? = lt
    fun eq(): String? = eq
    fun `in`(): Set<String>? = `in`
    fun like(): String? = like
    fun dateFilter(): DateFilterPeriod? = dateFilter

    interface Builder<T : Builder<T>> {

        fun le(le: String?): T

        fun ge(ge: String?): T

        fun gt(gt: String?): T

        fun lt(lt: String?): T

        fun eq(eq: String?): T

        fun `in`(`in`: Set<String>?): T

        fun like(like: String?): T

        fun dateFilter(dateFilter: DateFilterPeriod?): T

        fun isEmpty(isEmpty: Boolean?): T
    }
}
