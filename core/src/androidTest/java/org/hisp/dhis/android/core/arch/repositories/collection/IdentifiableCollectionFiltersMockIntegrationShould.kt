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
package org.hisp.dhis.android.core.arch.repositories.collection

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.period.DatePeriod
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class IdentifiableCollectionFiltersMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private val beforeDate = "2007-12-24T12:24:25.203"
    private val inBetweenDate = "2016-04-16T18:04:34.745"
    private val afterDate = "2017-12-24T12:24:25.203"

    private val birthUid = "m2jTvAj5kkm"
    private val defaultUid = "p0KPaWEg3cf"

    @Test
    fun find_objects_with_equal_name() {
        val combos = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun get_objects_with_equal_name_using_one() {
        val combo = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
            .one()
            .blockingGet()

        assertThat(combo!!.uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_children_with_equal_name() {
        val combos = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
            .withCategories()
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
        assertThat(combos[0].categories()!!.isEmpty()).isFalse()
    }

    @Test
    fun find_objects_with_equal_code() {
        val combos = d2.categoryModule().categoryCombos()
            .byCode().eq("BIRTHS")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_uid() {
        val combos = d2.categoryModule().categoryCombos()
            .byUid().eq("m2jTvAj5kkm")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_display_name() {
        val combos = d2.categoryModule().categoryCombos()
            .byDisplayName().eq("Display name with' 'single quo'tes'")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun do_not_find_objects_with_wrong_equal_name() {
        val combos = d2.categoryModule().categoryCombos()
            .byName().eq("Deaths")
            .blockingGet()

        assertThat(combos.isEmpty()).isTrue()
    }

    @Test
    fun do_not_find_objects_with_wrong_equal_code() {
        val combos = d2.categoryModule().categoryCombos()
            .byCode().eq("DEATHS")
            .blockingGet()

        assertThat(combos.isEmpty()).isTrue()
    }

    @Test
    fun find_objects_with_like_name() {
        val combos = d2.categoryModule().categoryCombos()
            .byName().like("bi")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_like_code() {
        val combos = d2.categoryModule().categoryCombos()
            .byCode().like("bi")
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_equal_created() {
        val created = DateUtils.DATE_FORMAT.parse("2011-12-24T12:24:25.203")
        val combos = d2.categoryModule().categoryCombos()
            .byCreated().eq(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_equal_last_updated() {
        val created = DateUtils.DATE_FORMAT.parse("2016-04-18T16:04:34.745")
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().eq(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_last_updated_before_date_before_both() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(0)
    }

    @Test
    fun find_objects_with_last_updated_before_date_in_between() {
        val created = DateUtils.DATE_FORMAT.parse(inBetweenDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(defaultUid)
    }

    @Test
    fun find_objects_with_last_updated_before_date_after_both() {
        val created = DateUtils.DATE_FORMAT.parse(afterDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().before(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_last_updated_after_date_before_both() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_last_updated_after_date_in_between() {
        val created = DateUtils.DATE_FORMAT.parse(inBetweenDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(1)
        assertThat(combos[0].uid()).isEqualTo(birthUid)
    }

    @Test
    fun find_objects_with_last_updated_after_date_after_both() {
        val created = DateUtils.DATE_FORMAT.parse(afterDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
            .blockingGet()

        assertThat(combos.size).isEqualTo(0)
    }

    @Test
    fun combine_date_and_string_filters() {
        val created = DateUtils.DATE_FORMAT.parse(beforeDate)
        val combos = d2.categoryModule().categoryCombos()
            .byLastUpdated().after(created)
            .byName().like("t")
            .blockingGet()

        assertThat(combos.size).isEqualTo(2)
    }

    @Test
    fun find_objects_with_last_updated_between_date_periods() {
        val before = DateUtils.DATE_FORMAT.parse(beforeDate)
        val inBetween = DateUtils.DATE_FORMAT.parse(inBetweenDate)
        val after = DateUtils.DATE_FORMAT.parse(afterDate)

        val beforeDatePeriods = listOf(DatePeriod.create(before, inBetween))
        val beforeCategories = d2.categoryModule().categories()
            .byLastUpdated().inDatePeriods(beforeDatePeriods)
            .blockingGet()
        assertThat(beforeCategories.size).isEqualTo(2)

        val afterDatePeriods = listOf(DatePeriod.create(inBetween, after))
        val afterCategories = d2.categoryModule().categories()
            .byLastUpdated().inDatePeriods(afterDatePeriods)
            .blockingGet()
        assertThat(afterCategories.size).isEqualTo(2)

        val datePeriods = listOf(
            DatePeriod.create(before, inBetween),
            DatePeriod.create(inBetween, after),
        )
        val categories = d2.categoryModule().categories()
            .byLastUpdated().inDatePeriods(datePeriods).blockingGet()
        assertThat(categories.size).isEqualTo(4)
    }

    @Test
    fun find_objects_with_last_updated_between_periods() {
        val period = Period.builder().periodId("201710")
            .startDate(DateUtils.DATE_FORMAT.parse("2017-10-01T00:00:00.000"))
            .endDate(DateUtils.DATE_FORMAT.parse("2017-10-31T23:59:59.999"))
            .periodType(PeriodType.Monthly)
            .build()

        val beforeCategories = d2.categoryModule().categories()
            .byLastUpdated().inPeriods(listOf(period)).blockingGet()

        assertThat(beforeCategories.size).isEqualTo(1)
    }

    @Test
    fun find_object_with_uid_shortcut_method() {
        val combo = d2.categoryModule().categoryCombos()
            .uid(birthUid)
            .blockingGet()

        assertThat(combo).isNotNull()
        assertThat(combo!!.uid()).isEqualTo(birthUid)
    }

    @Test
    fun do_not_fail_with_uid_containing_single_quote() {
        val combo = d2.categoryModule().categoryCombos()
            .uid("non'existing'uid'")
            .blockingGet()

        assertThat(combo).isNull()
    }
}
