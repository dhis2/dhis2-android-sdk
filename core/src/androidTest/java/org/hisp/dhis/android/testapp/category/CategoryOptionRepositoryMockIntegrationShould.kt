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
package org.hisp.dhis.android.testapp.category

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class CategoryOptionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val options = d2.categoryModule().categoryOptions().blockingGet()

        assertThat(options.size).isEqualTo(8)
    }

    @Test
    fun filter_by_start_date() {
        val date = DateUtils.DATE_FORMAT.parse("2012-12-24T12:24:24.000")
        val options = d2.categoryModule().categoryOptions()
            .byStartDate().eq(date)
            .blockingGet()

        assertThat(options.size).isEqualTo(1)
    }

    @Test
    fun filter_by_end_date() {
        val date = DateUtils.DATE_FORMAT.parse("2013-12-24T12:24:24.777")
        val options = d2.categoryModule().categoryOptions()
            .byEndDate().eq(date)
            .blockingGet()

        assertThat(options.size).isEqualTo(1)
    }

    @Test
    fun filter_by_access_data_write() {
        val options = d2.categoryModule().categoryOptions()
            .byAccessDataWrite().isTrue
            .blockingGet()

        assertThat(options.size).isEqualTo(5)
    }

    @Test
    fun filter_by_category_uid() {
        val options = d2.categoryModule().categoryOptions()
            .byCategoryUid("KfdsGBcoiCa")
            .blockingGet()

        assertThat(options.size).isEqualTo(3)
    }

    @Test
    fun filter_by_category_option_combo_uid() {
        val options = d2.categoryModule().categoryOptions()
            .byCategoryOptionComboUid("Gmbgme7z9BF")
            .blockingGet()

        assertThat(options.size).isEqualTo(2)
    }
}
