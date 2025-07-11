/*
 *  Copyright (c) 2004-2024, University of Oslo
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
package org.hisp.dhis.android.network.common.filters

import org.hisp.dhis.android.network.common.fields.Field

internal class SingleValueFilter<T> private constructor(
    override val field: Field<T>,
    override val operator: String,
    override val values: Collection<String>,
) : Filter<T> {
    override fun generateString(): String {
        return "${field.name}:$operator:${values.first()}"
    }

    companion object {
        private fun <T> create(
            field: Field<T>,
            operator: String,
            value: String,
        ): Filter<T> {
            // If the filter is incomplete, return null so the filter is not included in the request.
            return SingleValueFilter(field, operator, listOf(value))
        }

        fun <T> gt(field: Field<T>, value: String): Filter<T> {
            return create(field, "gt", value)
        }

        fun <T> eq(field: Field<T>, value: String): Filter<T> {
            return create(field, "eq", value)
        }

        fun <T> like(field: Field<T>, value: String): Filter<T> {
            return create(field, "like", value)
        }
    }
}
