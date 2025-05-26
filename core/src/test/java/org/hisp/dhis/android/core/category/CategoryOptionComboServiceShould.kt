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
package org.hisp.dhis.android.core.category

import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CategoryOptionComboServiceShould {

    private val categoryOptionComboUid: String = "categoryOptionComboUid"

    private val categoryOptionRepository: CategoryOptionCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val categoryOptionComboService = CategoryOptionComboService(categoryOptionRepository)

    private val option1: CategoryOption = mock()
    private val option2: CategoryOption = mock()

    // Dates
    private val firstJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    private val secondJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-02T00:00:00.000")
    private val thirdJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-03T00:00:00.000")

    @Before
    fun setUp() {
        whenever(
            categoryOptionRepository
                .byCategoryOptionComboUid(categoryOptionComboUid)
                .blockingGet(),
        ) doReturn listOf(option1, option2)

        whenever(option1.access()) doReturn AccessHelper.createForDataWrite(true)
        whenever(option2.access()) doReturn AccessHelper.createForDataWrite(true)
    }

    @Test
    fun `Should return true if has access and no date range`() {
        assertTrue(categoryOptionComboService.blockingHasAccess(categoryOptionComboUid, null))
    }

    @Test
    fun `Should return false if has not access and no date range`() {
        whenever(option2.access()) doReturn AccessHelper.createForDataWrite(false)
        assertFalse(categoryOptionComboService.blockingHasAccess(categoryOptionComboUid, null))
    }

    @Test
    fun `Should return true if event date is null`() {
        whenever(option1.startDate()) doReturn firstJanuary
        whenever(option1.endDate()) doReturn thirdJanuary
        assertTrue(categoryOptionComboService.blockingHasAccess(categoryOptionComboUid, null))
    }

    @Test
    fun `Should return true if event date within option date range`() {
        whenever(option1.startDate()) doReturn firstJanuary
        whenever(option1.endDate()) doReturn thirdJanuary
        assertTrue(categoryOptionComboService.blockingHasAccess(categoryOptionComboUid, secondJanuary))
    }

    @Test
    fun `Should return false if event date out of option date range`() {
        whenever(option1.startDate()) doReturn firstJanuary
        whenever(option1.endDate()) doReturn secondJanuary
        assertFalse(categoryOptionComboService.blockingHasAccess(categoryOptionComboUid, thirdJanuary))
    }
}
