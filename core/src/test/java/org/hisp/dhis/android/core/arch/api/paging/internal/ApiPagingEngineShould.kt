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
package org.hisp.dhis.android.core.arch.api.paging.internal

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine.calculateFirstPagination
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine.calculateLastPagination
import org.hisp.dhis.android.core.arch.api.paging.internal.ApiPagingEngine.getPaginationList
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.util.Arrays

@RunWith(MockitoJUnitRunner::class)
class ApiPagingEngineShould {
    @Test
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun calculate_a_paging_list() {
        var calculatedPagingList: List<Paging?> = getPaginationList(50, 179, 0)
        var paging1 = Paging(1, 50, 0, 0, false)
        var paging2 = Paging(2, 50, 0, 0, false)
        var paging3 = Paging(3, 50, 0, 0, false)
        var lastPaging = Paging(6, 30, 0, 1, true)

        var expectedPagingList: List<Paging?> =
            ArrayList(Arrays.asList(paging1, paging2, paging3, lastPaging))

        Truth.assertThat(expectedPagingList).isEqualTo(calculatedPagingList)
    }

    @Test
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun calculate_a_paging_list_if_only_one_page() {
        var calculatedPagingList: List<Paging?> = getPaginationList(50, 33, 0)
        var paging = Paging(1, 33, 0, 0, true)
        var expectedPagingList: List<Paging?> = ArrayList(listOf(paging))

        Truth.assertThat(expectedPagingList).isEqualTo(calculatedPagingList)
    }

    @Test
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun calculate_a_paging_list_with_previously_downloaded() {
        var calculatedPagingList: List<Paging?> = getPaginationList(50, 85, 94)
        var paging1 = Paging(10, 10, 4, 0, false)
        var paging2 = Paging(3, 50, 0, 0, false)
        var lastPaging = Paging(6, 30, 0, 1, true)

        var expectedPagingList: List<Paging?> =
            ArrayList(Arrays.asList(paging1, paging2, lastPaging))

        Truth.assertThat(expectedPagingList).isEqualTo(calculatedPagingList)

        calculatedPagingList = getPaginationList(50, 4, 7)
        paging1 = Paging(2, 6, 1, 1, false)

        expectedPagingList = ArrayList(listOf(paging1))

        Truth.assertThat(expectedPagingList).isEqualTo(calculatedPagingList)
    }

    @Test
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun calculate_first_pagination() {
        var calculatedPaging = calculateFirstPagination(50, 1, 13, 87)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(6, 17, 2, 2, false))

        calculatedPaging = calculateFirstPagination(50, 1, 10, 90)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(10, 10, 0, 0, false))

        calculatedPaging = calculateFirstPagination(20, 10, 3, 217)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(55, 4, 1, 0, false))

        calculatedPaging = calculateFirstPagination(50, 0, 50, 0)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(1, 50, 0, 0, false))

        calculatedPaging = calculateFirstPagination(50, 0, 4, 7)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(2, 6, 1, 1, false))
    }

    @Test
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun calculate_last_paging() {
        var calculatedPaging = calculateLastPagination(50, 179, 4)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(6, 30, 0, 1, true))

        calculatedPaging = calculateLastPagination(10, 5, 1)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(1, 5, 0, 0, true))

        calculatedPaging = calculateLastPagination(100, 32, 1)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(1, 32, 0, 0, true))

        calculatedPaging = calculateLastPagination(50, 1688, 34)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(36, 47, 5, 4, true))

        calculatedPaging = calculateLastPagination(130, 536, 5)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(27, 20, 0, 4, true))

        calculatedPaging = calculateLastPagination(50, 50, 1)
        Truth.assertThat(calculatedPaging).isEqualTo(Paging(1, 50, 0, 0, true))
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(IllegalArgumentException::class)
    fun throw_exception_if_current_page_is_negative() {
        getPaginationList(-30, 179, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(IllegalArgumentException::class)
    fun throw_exception_if_page_is_negative() {
        getPaginationList(50, -50, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(IllegalArgumentException::class)
    fun throw_exception_if_current_page_is_zero() {
        getPaginationList(0, 30, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    @Throws(IllegalArgumentException::class)
    fun throw_exception_if_page_is_zero() {
        getPaginationList(120, 0, 0)
    }
}
