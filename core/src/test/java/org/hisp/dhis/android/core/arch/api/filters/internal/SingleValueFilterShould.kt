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

package org.hisp.dhis.android.core.arch.api.filters.internal

import org.hisp.dhis.android.network.common.fields.Field
import org.hisp.dhis.android.network.common.filters.SingleValueFilter
import org.junit.Assert.*
import org.junit.Test
class SingleValueFilterShould {

    private val dummyFieldName = "test_field_name"
    private val dummyField = Field.create<String>(dummyFieldName)
    private val dummyValue = "test_value"

    @Test
    fun gt_methods_returns_correct_instance() {
        val filter = SingleValueFilter.gt(dummyField, dummyValue)
        assertNotNull(filter)
        filter as SingleValueFilter
        assertEquals(dummyField, filter.field)
        assertEquals("gt", filter.operator)
        assertEquals(listOf(dummyValue), filter.values)
    }

    @Test
    fun eq_methods_returns_correct_instance() {
        val filter = SingleValueFilter.eq(dummyField, dummyValue)
        assertNotNull(filter)
        filter as SingleValueFilter
        assertEquals(dummyField, filter.field)
        assertEquals("eq", filter.operator)
        assertEquals(listOf(dummyValue), filter.values)
    }

    @Test
    fun like_methods_returns_correct_instance() {
        val filter = SingleValueFilter.like(dummyField, dummyValue)
        assertNotNull(filter)
        filter as SingleValueFilter
        assertEquals(dummyField, filter.field)
        assertEquals("like", filter.operator)
        assertEquals(listOf(dummyValue), filter.values)
    }

    @Test
    fun gt_generateString_returns_correct_string() {
        val filter = SingleValueFilter.gt(dummyField, dummyValue)
        val actualString = filter.generateString()
        val expectedString = "$dummyFieldName:gt:$dummyValue"
        assertEquals(expectedString, actualString)
    }

    @Test
    fun eq_generateString_returns_correct_string() {
        val filter = SingleValueFilter.eq(dummyField, dummyValue)
        val actualString = filter.generateString()
        val expectedString = "$dummyFieldName:eq:$dummyValue"
        assertEquals(expectedString, actualString)
    }

    @Test
    fun like_generateString_returns_correct_string() {
        val filter = SingleValueFilter.like(dummyField, dummyValue)
        val actualString = filter.generateString()
        val expectedString = "$dummyFieldName:like:$dummyValue"
        assertEquals(expectedString, actualString)
    }
}
