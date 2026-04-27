/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.program.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.program.CategoryMapping
import org.hisp.dhis.android.core.program.CategoryOptionMapping
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class CategoryMappingHandlerShould {
    private val categoryMappingStore: CategoryMappingStore = mock()
    private val categoryOptionMappingHandler: CategoryOptionMappingHandler = mock()

    private lateinit var categoryMappingHandler: CategoryMappingHandler

    @Before
    fun setUp() {
        categoryMappingHandler = CategoryMappingHandler(
            categoryMappingStore,
            categoryOptionMappingHandler,
        )
    }

    @Test
    fun call_option_mapping_handler_after_handling_category_mapping() = runTest {
        val optionMapping = CategoryOptionMapping.builder()
            .categoryMapping("mapping1")
            .optionId("option1")
            .filter("#{condition}")
            .build()

        val categoryMapping = CategoryMapping.builder()
            .uid("mapping1")
            .program("program1")
            .categoryId("category1")
            .mappingName("Test mapping")
            .optionMappings(listOf(optionMapping))
            .build()

        categoryMappingHandler.handleMany("program1", listOf(categoryMapping)) { it }

        verify(categoryOptionMappingHandler).handleMany(eq("mapping1"), any(), any())
    }
}
