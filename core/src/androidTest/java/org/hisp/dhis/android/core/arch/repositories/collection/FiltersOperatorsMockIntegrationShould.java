/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class FiltersOperatorsMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    private final String NAME_1 = "Antenatal care visit - Program rules demo";
    private final String NAME_2 = "Child care visit - demo";

    @Test
    public void filter_string_with_eq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byName().eq(NAME_1);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_string_with_neq_for_non_existent() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byName().neq("Non existent");
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_string_with_neq_for_non_existing() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byName().neq(NAME_1);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_boolean_with_eq_true() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byAllowGenerateNextVisit().eq(true);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_boolean_with_eq_false() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byAllowGenerateNextVisit().eq(false);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_boolean_with_neq_true() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byAllowGenerateNextVisit().neq(true);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_boolean_with_neq_false() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byAllowGenerateNextVisit().neq(false);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_int_with_eq_0() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byMinDaysFromStart().eq(0);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_int_with_eq_1() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byMinDaysFromStart().eq(1);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_int_with_neq_0() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byMinDaysFromStart().neq(0);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_int_with_neq_1() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byMinDaysFromStart().neq(1);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_enum_with_eq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byPeriodType().eq(PeriodType.Monthly);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_enum_with_neq() {
        ProgramStageCollectionRepository repository = d2.programModule().programStages
                .byPeriodType().neq(PeriodType.Monthly);
        List<ProgramStage> objects = repository.get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_date_with_eq() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T15:30:43.806");
        List<ProgramStage> objects = d2.programModule().programStages
                .byCreated().eq(created)
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_date_with_neq() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T15:30:43.806");
        List<ProgramStage> objects = d2.programModule().programStages
                .byCreated().neq(created)
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_string_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().in(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(0));
    }

    @Test
    public void filter_string_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().in(Collections.singletonList(NAME_1))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_string_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().in(Arrays.asList(NAME_1, NAME_2))
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_string_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().notIn(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_string_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().notIn(Collections.singletonList(NAME_1))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_string_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byName().notIn(Arrays.asList(NAME_1, NAME_2))
                .get();
        assertThat(objects.size(), is(0));
    }

    @Test
    public void filter_int_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().in(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(0));
    }

    @Test
    public void filter_int_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().in(Collections.singletonList(0))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_int_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().in(Arrays.asList(0, 1))
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_int_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().notIn(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_int_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().notIn(Collections.singletonList(0))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_int_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byMinDaysFromStart().notIn(Arrays.asList(0, 1))
                .get();
        assertThat(objects.size(), is(0));
    }

    @Test
    public void filter_boolean_with_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().in(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(0));
    }

    @Test
    public void filter_boolean_int_with_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().in(Collections.singletonList(false))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_1));
    }

    @Test
    public void filter_boolean_int_with_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().in(Arrays.asList(false, true))
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_boolean_int_with_not_in_with_no_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().notIn(Collections.emptyList())
                .get();
        assertThat(objects.size(), is(2));
    }

    @Test
    public void filter_boolean_int_with_not_in_with_one_element() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().notIn(Collections.singletonList(false))
                .get();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).name(), is(NAME_2));
    }

    @Test
    public void filter_boolean_int_with_not_in_with_two_elements() {
        List<ProgramStage> objects = d2.programModule().programStages
                .byAllowGenerateNextVisit().notIn(Arrays.asList(false, true))
                .get();
        assertThat(objects.size(), is(0));
    }
}