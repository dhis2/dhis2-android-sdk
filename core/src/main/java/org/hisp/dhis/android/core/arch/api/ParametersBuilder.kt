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

package org.hisp.dhis.android.core.arch.api

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter

class ParametersBuilder(var parameters: MutableList<Pair<String, String>>) {
    internal fun <T> fields(fields: Fields<T>) {
        parameters.add("fields" to fields.generateString())
    }

    internal fun <T> filter(filter: Filter<T>?) {
        filter?.let {
            parameters.add("filter" to filter.generateString())
        }
    }

    fun filter(filter: List<String?>?) {
        filter?.let {
            parameters.add("filter" to it.filterNot { it.isNullOrBlank() }.joinToString(","))
        }
    }

    fun attribute(attribute: String, value: String?) {
        value?.let { parameters.add(attribute to it) }
    }

    fun attribute(attribute: String, value: Int?) {
        value?.let { parameters.add(attribute to it.toString()) }
    }

    fun attribute(attribute: String, value: Boolean?) {
        value?.let { parameters.add(attribute to it.toString()) }
    }

    fun attribute(attribute: String, value: List<String?>?) {
        value?.let { it.filterNotNull().forEach { parameters.add(attribute to it) } }
    }

    fun paging(paging: Boolean) {
        parameters.add("paging" to paging.toString())
    }

    fun page(page: Int?) {
        page?.let { parameters.add("page" to it.toString()) }
    }

    fun pageSize(pageSize: Int?) {
        pageSize?.let { parameters.add("pageSize" to it.toString()) }
    }
}
