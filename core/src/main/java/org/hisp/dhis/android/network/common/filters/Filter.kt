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

package org.hisp.dhis.android.network.common.filters

import kotlinx.serialization.serializer
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.network.common.fields.requirePropertyPath

/**
 * A `filter` request parameter, generated as `field:operator:value`. The field is the API property name, or a path to
 * a nested property like `program.id`, and must exist in the DTO the request returns, so a typo or a name that does
 * not match the DTO fails instead of filtering on the wrong property.
 */
internal data class Filter @PublishedApi internal constructor(
    val field: String,
    val operator: String,
    val value: String,
) {
    fun generateString(): String = "$field:$operator:$value"

    companion object {
        inline fun <reified DTO> eq(field: String, value: Any): Filter = create<DTO>(field, "eq", value.toString())

        inline fun <reified DTO> gt(field: String, value: String): Filter = create<DTO>(field, "gt", value)

        inline fun <reified DTO> like(field: String, value: String): Filter = create<DTO>(field, "like", value)

        inline fun <reified DTO> `in`(field: String, values: Collection<String>): Filter =
            create<DTO>(field, "in", inValue(values))

        fun uidIn(uids: Collection<String>): Filter = Filter(BaseIdentifiableObject.UID, "in", inValue(uids))

        /**
         * For filters on server properties the DTO does not declare because the SDK never reads them, so the field
         * cannot be checked.
         */
        fun inUnchecked(field: String, values: Collection<String>): Filter = Filter(field, "in", inValue(values))

        @PublishedApi
        internal inline fun <reified DTO> create(field: String, operator: String, value: String): Filter {
            serializer<DTO>().descriptor.requirePropertyPath(field)
            return Filter(field, operator, value)
        }

        @PublishedApi
        internal fun inValue(values: Collection<String>): String = values.joinToString(",", prefix = "[", postfix = "]")
    }
}
