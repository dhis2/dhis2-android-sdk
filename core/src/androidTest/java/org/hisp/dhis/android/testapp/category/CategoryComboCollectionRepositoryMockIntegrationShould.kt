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
package org.hisp.dhis.android.testapp.category

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

class CategoryComboCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    private val beforeDate = "2007-12-24T12:24:25.203"
    private val inBetweenDate = "2016-04-16T18:04:34.745"
    private val afterDate = "2017-12-24T12:24:25.203"
    private val birthUid = "m2jTvAj5kkm"
    private val defaultUid = "p0KPaWEg3cf"

    @Test
    fun find_objects_with_equal_name() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_children_with_equal_name() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
        val combos = repositoryWithUpdatedScope.withCategories().blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
        assertThat(combos[0].categories()!!).isNotEmpty()
    }

    @Test
    fun find_objects_with_equal_code() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byCode().eq("BIRTHS")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_uid() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byUid().eq("m2jTvAj5kkm")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_display_name() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byDisplayName().eq("Display name with' 'single quo'tes'")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun do_not_find_objects_with_wrong_equal_name() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byName().eq("Deaths")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos).isEmpty()
    }

    @Test
    fun do_not_find_objects_with_wrong_equal_code() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byCode().eq("DEATHS")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos).isEmpty()
    }

    @Test
    fun find_objects_with_like_name() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byName().like("bi")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_like_code() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byCode().like("bi")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_created() {
        val created = DateUtils.DATE_FORMAT.parse("2011-12-24T12:24:25.203")
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byCreated().eq(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_equal_last_updated() {
        val created = DateUtils.DATE_FORMAT.parse("2016-04-18T16:04:34.745")
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().eq(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_last_updated_before_date_before_both() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(0)
    }

    @Test
    fun find_objects_with_last_updated_before_date_in_between() {
        val created = DateUtils.DATE_FORMAT.parse(inBetweenDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(defaultUid)
    }

    @Test
    fun find_objects_with_last_updated_before_date_after_both() {
        val created = DateUtils.DATE_FORMAT.parse(afterDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_last_updated_after_date_before_both() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_last_updated_after_date_in_between() {
        val created = DateUtils.DATE_FORMAT.parse(inBetweenDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_last_updated_after_date_after_both() {
        val created = DateUtils.DATE_FORMAT.parse(afterDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(0)
    }

    @Test
    fun combine_date_and_string_filters() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
            .byName().like("t")
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_non_default_category_combo() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byIsDefault().isFalse
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo("m2jTvAj5kkm")
    }

    @Test
    fun find_default_category_combo() {
        val repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
            .byIsDefault().isTrue
        val combos = repositoryWithUpdatedScope.blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo("p0KPaWEg3cf")
    }

    @Test
    fun include_categories_as_children() {
        val categoryCombo = d2.categoryModule().categoryCombos()
            .withCategories()
            .uid("m2jTvAj5kkm")
            .blockingGet()!!

        assertThat(categoryCombo.categories()!!.size).isEqualTo(2)
    }
}
