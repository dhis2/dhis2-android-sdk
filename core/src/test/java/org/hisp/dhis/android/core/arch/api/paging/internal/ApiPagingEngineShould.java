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

package org.hisp.dhis.android.core.arch.api.paging.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ApiPagingEngineShould {

    @Test
    public void calculate_a_paging_list() throws IllegalArgumentException, IllegalStateException {
        List<Paging> calculatedPagingList = ApiPagingEngine.getPaginationList(50, 179, 0);
        Paging paging1 = Paging.create(1,50,0, 0, false);
        Paging paging2 = Paging.create(2,50,0, 0, false);
        Paging paging3 = Paging.create(3,50,0, 0, false);
        Paging lastPaging = Paging.create(6,30,0, 1, true);

        List<Paging> expectedPagingList = new ArrayList<>(Arrays.asList(paging1, paging2, paging3, lastPaging));

        assertThat(expectedPagingList).isEqualTo(calculatedPagingList);
    }

    @Test
    public void calculate_a_paging_list_if_only_one_page() throws IllegalArgumentException, IllegalStateException {
        List<Paging> calculatedPagingList = ApiPagingEngine.getPaginationList(50, 33, 0);
        Paging paging = Paging.create(1,33,0, 0, true);
        List<Paging> expectedPagingList = new ArrayList<>(Collections.singletonList(paging));

        assertThat(expectedPagingList).isEqualTo(calculatedPagingList);
    }

    @Test
    public void calculate_a_paging_list_with_previously_downloaded() throws IllegalArgumentException, IllegalStateException {
        List<Paging> calculatedPagingList = ApiPagingEngine.getPaginationList(50, 85, 94);
        Paging paging1 = Paging.create(10,10,4, 0, false);
        Paging paging2 = Paging.create(3,50,0, 0, false);
        Paging lastPaging = Paging.create(6,30,0, 1, true);

        List<Paging> expectedPagingList = new ArrayList<>(Arrays.asList(paging1, paging2, lastPaging));

        assertThat(expectedPagingList).isEqualTo(calculatedPagingList);

        calculatedPagingList = ApiPagingEngine.getPaginationList(50, 4, 7);
        paging1 = Paging.create(2,6,1, 1, false);

        expectedPagingList = new ArrayList<>(Collections.singletonList(paging1));

        assertThat(expectedPagingList).isEqualTo(calculatedPagingList);
    }

    @Test
    public void calculate_first_pagination() throws IllegalArgumentException, IllegalStateException {
        Paging calculatedPaging = ApiPagingEngine.calculateFirstPagination(
                50, 1, 13, 87);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                6,17, 2, 2, false));

        calculatedPaging = ApiPagingEngine.calculateFirstPagination(
                50, 1, 10, 90);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                10,10, 0, 0, false));

        calculatedPaging = ApiPagingEngine.calculateFirstPagination(
                20, 10, 3, 217);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                55,4, 1, 0, false));

        calculatedPaging = ApiPagingEngine.calculateFirstPagination(
                50, 0, 50, 0);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                1,50, 0, 0, false));

        calculatedPaging = ApiPagingEngine.calculateFirstPagination(
                50, 0, 4, 7);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                2,6, 1, 1, false));
    }

    @Test
    public void calculate_last_paging() throws IllegalArgumentException, IllegalStateException {
        Paging calculatedPaging = ApiPagingEngine.calculateLastPagination(
                50, 179, 4);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                6,30, 0, 1, true));

        calculatedPaging = ApiPagingEngine.calculateLastPagination(10, 5, 1);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                1,5,0, 0, true));

        calculatedPaging = ApiPagingEngine.calculateLastPagination(100, 32, 1);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                1,32,0, 0, true));

        calculatedPaging = ApiPagingEngine.calculateLastPagination(50, 1688, 34);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                36,47,5, 4, true));

        calculatedPaging = ApiPagingEngine.calculateLastPagination(130, 536,5);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                27,20,0, 4, true));

        calculatedPaging = ApiPagingEngine.calculateLastPagination(50, 50,1);
        assertThat(calculatedPaging).isEqualTo(Paging.create(
                1,50,0, 0, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_exception_if_current_page_is_negative() throws IllegalArgumentException {
        ApiPagingEngine.getPaginationList(-30, 179, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_exception_if_page_is_negative() throws IllegalArgumentException {
        ApiPagingEngine.getPaginationList(50, -50, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_exception_if_current_page_is_zero() throws IllegalArgumentException {
        ApiPagingEngine.getPaginationList(0, 30, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_exception_if_page_is_zero() throws IllegalArgumentException {
        ApiPagingEngine.getPaginationList(120, 0, 0);
    }
}