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
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.text.ParseException

@RunWith(D2JunitRunner::class)
class FiltersOperatorsMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private val name1 = "Antenatal care visit - Program rules demo"
    private val name2 = "Child care visit - demo"

    @Test
    fun filter_string_with_eq() {
        val objects = d2.programModule().programStages()
            .byName().eq(name1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_string_with_neq_for_non_existent() {
        val objects = d2.programModule().programStages()
            .byName().neq("Non existent")
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_string_with_neq_for_non_existing() {
        val objects = d2.programModule().programStages()
            .byName().neq(name1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_boolean_with_eq_true() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().eq(true)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_boolean_with_eq_false() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().eq(false)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_boolean_with_neq_true() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().neq(true)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_boolean_with_neq_false() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().neq(false)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_int_with_eq_0() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().eq(0)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_int_with_eq_1() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().eq(1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_int_with_neq_0() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().neq(0)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_int_with_neq_1() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().neq(1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_enum_with_eq() {
        val objects = d2.programModule().programStages()
            .byPeriodType().eq(PeriodType.Monthly)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_enum_with_neq() {
        val objects = d2.programModule().programStages()
            .byPeriodType().neq(PeriodType.Monthly)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_date_with_eq() {
        val created = DateUtils.DATE_FORMAT.parse("2016-04-12T15:30:43.806")
        val objects = d2.programModule().programStages()
            .byCreated().eq(created)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    @Throws(ParseException::class)
    fun filter_date_with_neq() {
        val created = DateUtils.DATE_FORMAT.parse("2016-04-12T15:30:43.806")
        val objects = d2.programModule().programStages()
            .byCreated().neq(created)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_string_with_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byName().`in`(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_string_with_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byName().`in`(listOf(name1))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_string_with_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byName().`in`(listOf(name1, name2))
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_string_with_not_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byName().notIn(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_string_with_not_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byName().notIn(listOf(name1))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_string_with_not_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byName().notIn(listOf(name1, name2))
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_string_with_varargs_with_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byName().`in`()
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_string_with_varargs_with_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byName().`in`(name1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_string_with_varargs_with_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byName().`in`(name1, name2)
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_string_with_varargs_with_not_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byName().notIn()
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_string_with_varargs_with_not_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byName().notIn(name1)
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_string_with_varargs_with_not_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byName().notIn(name1, name2)
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_int_with_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().`in`(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_int_with_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().`in`(listOf(0))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_int_with_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().`in`(listOf(0, 1))
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_int_with_not_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().notIn(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_int_with_not_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().notIn(listOf(0))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_int_with_not_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byMinDaysFromStart().notIn(listOf(0, 1))
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_boolean_with_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().`in`(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_boolean_int_with_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().`in`(listOf(false))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_boolean_int_with_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().`in`(listOf(false, true))
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_boolean_int_with_not_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().notIn(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_boolean_int_with_not_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().notIn(listOf(false))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_boolean_int_with_not_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byAllowGenerateNextVisit().notIn(listOf(false, true))
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_enum_with_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byPeriodType().`in`(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_enum_int_with_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byPeriodType().`in`(listOf(PeriodType.Monthly))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name1)
    }

    @Test
    fun filter_enum_int_with_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byPeriodType().`in`(listOf(PeriodType.Monthly, PeriodType.Weekly))
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_enum_int_with_not_in_with_no_elements() {
        val objects = d2.programModule().programStages()
            .byPeriodType().notIn(emptyList())
            .blockingGet()

        assertThat(objects.size).isEqualTo(2)
    }

    @Test
    fun filter_enum_int_with_not_in_with_one_element() {
        val objects = d2.programModule().programStages()
            .byPeriodType().notIn(listOf(PeriodType.Monthly))
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
        assertThat(objects[0].name()).isEqualTo(name2)
    }

    @Test
    fun filter_enum_int_with_not_in_with_two_elements() {
        val objects = d2.programModule().programStages()
            .byPeriodType().notIn(listOf(PeriodType.Monthly, PeriodType.Weekly))
            .blockingGet()

        assertThat(objects.size).isEqualTo(0)
    }

    @Test
    fun filter_string_with_is_null() {
        val objects = d2.programModule().programStages()
            .byDescription().isNull
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
    }

    @Test
    fun filter_string_with_is_not_null() {
        val objects = d2.programModule().programStages()
            .byDescription().isNotNull
            .blockingGet()

        assertThat(objects.size).isEqualTo(1)
    }
}
