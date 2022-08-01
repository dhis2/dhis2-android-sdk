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

package org.hisp.dhis.android.core.arch.repositories.collection;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class FiltersOperatorsMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    private final String NAME_1 = "Antenatal care visit - Program rules demo";
    private final String NAME_2 = "Child care visit - demo";

    @Test
    public void filter_string_with_eq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byName().eq(NAME_1);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_string_with_neq_for_non_existent() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byName().neq("Non existent");
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_string_with_neq_for_non_existing() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byName().neq(NAME_1);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_boolean_with_eq_true() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byAllowGenerateNextVisit().eq(true);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_boolean_with_eq_false() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byAllowGenerateNextVisit().eq(false);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_boolean_with_neq_true() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byAllowGenerateNextVisit().neq(true);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_boolean_with_neq_false() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byAllowGenerateNextVisit().neq(false);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_int_with_eq_0() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byMinDaysFromStart().eq(0);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_int_with_eq_1() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byMinDaysFromStart().eq(1);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_int_with_neq_0() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byMinDaysFromStart().neq(0);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_int_with_neq_1() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byMinDaysFromStart().neq(1);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_enum_with_eq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byPeriodType().eq(PeriodType.Monthly);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_enum_with_neq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byPeriodType().neq(PeriodType.Monthly);
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_date_with_eq() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T15:30:43.806");
        List<ProgramStage> objects = d2.programModule().programStages()
                .byCreated().eq(created)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_date_with_neq() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T15:30:43.806");
        List<ProgramStage> objects = d2.programModule().programStages()
                .byCreated().neq(created)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_string_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_string_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in(Collections.singletonList(NAME_1))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_string_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in(Arrays.asList(NAME_1, NAME_2))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_string_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_string_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn(Collections.singletonList(NAME_1))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_string_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn(Arrays.asList(NAME_1, NAME_2))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_string_with_varargs_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in()
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_string_with_varargs_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in(NAME_1)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_string_with_varargs_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().in(NAME_1, NAME_2)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_string_with_varargs_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn()
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_string_with_varargs_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn(NAME_1)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_string_with_varargs_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byName().notIn(NAME_1, NAME_2)
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_int_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().in(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_int_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().in(Collections.singletonList(0))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_int_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().in(Arrays.asList(0, 1))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_int_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().notIn(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_int_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().notIn(Collections.singletonList(0))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_int_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byMinDaysFromStart().notIn(Arrays.asList(0, 1))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_boolean_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().in(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_boolean_int_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().in(Collections.singletonList(false))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_boolean_int_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().in(Arrays.asList(false, true))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_boolean_int_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().notIn(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_boolean_int_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().notIn(Collections.singletonList(false))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_boolean_int_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byAllowGenerateNextVisit().notIn(Arrays.asList(false, true))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_enum_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().in(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_enum_int_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().in(Collections.singletonList(PeriodType.Monthly))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_1);
    }

    @Test
    public void filter_enum_int_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().in(Arrays.asList(PeriodType.Monthly, PeriodType.Weekly))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_enum_int_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().notIn(Collections.emptyList())
                .blockingGet();
        assertThat(objects.size()).isEqualTo(2);
    }

    @Test
    public void filter_enum_int_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().notIn(Collections.singletonList(PeriodType.Monthly))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(1);
        assertThat(objects.get(0).name()).isEqualTo(NAME_2);
    }

    @Test
    public void filter_enum_int_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages()
                .byPeriodType().notIn(Arrays.asList(PeriodType.Monthly, PeriodType.Weekly))
                .blockingGet();
        assertThat(objects.size()).isEqualTo(0);
    }

    @Test
    public void filter_string_with_is_null() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byDescription().isNull();
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
    }

    @Test
    public void filter_string_with_is_not_null() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages()
                .byDescription().isNotNull();
        List<ProgramStage> objects = repository.blockingGet();
        assertThat(objects.size()).isEqualTo(1);
    }

}