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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyOneObjectRepositoryFinalImpl;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboCollectionRepository;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.DatePeriod;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class IdentifiableCollectionFiltersMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    private final String BEFORE_DATE = "2007-12-24T12:24:25.203";
    private final String IN_BETWEEN_DATE = "2016-04-16T18:04:34.745";
    private final String AFTER_DATE =  "2017-12-24T12:24:25.203";

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @Test
    public void find_objects_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void get_objects_with_equal_name_using_one() {
        ReadOnlyOneObjectRepositoryFinalImpl<CategoryCombo> objectRepository = d2.categoryModule().categoryCombos()
                .byName().eq("Births")
                .one();
        CategoryCombo combo = objectRepository.blockingGet();
        assertThat(combo.uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_children_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.withCategories().blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
        assertThat(combos.get(0).categories().isEmpty()).isFalse();
    }

    @Test
    public void find_objects_with_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byCode().eq("BIRTHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_equal_uid() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byUid().eq("m2jTvAj5kkm");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_equal_display_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byDisplayName().eq("Display name with' 'single quo'tes'");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byName().eq("Deaths");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.isEmpty()).isTrue();
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byCode().eq("DEATHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.isEmpty()).isTrue();
    }

    @Test
    public void find_objects_with_like_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byName().like("bi");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_like_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byCode().like("bi");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_equal_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.203");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byCreated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(2);
    }

    @Test
    public void find_objects_with_equal_last_updated() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-18T16:04:34.745");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_last_updated_before_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(0);
    }

    @Test
    public void find_objects_with_last_updated_before_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(DEFAULT_UID);
    }

    @Test
    public void find_objects_with_last_updated_before_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(2);
    }

    @Test
    public void find_objects_with_last_updated_after_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(2);
    }

    @Test
    public void find_objects_with_last_updated_after_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(1);
        assertThat(combos.get(0).uid()).isEqualTo(BIRTH_UID);
    }

    @Test
    public void find_objects_with_last_updated_after_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(0);
    }

    @Test
    public void combine_date_and_string_filters() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos()
                .byLastUpdated().after(created)
                .byName().like("t");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.blockingGet();
        assertThat(combos.size()).isEqualTo(2);
    }

    @Test
    public void find_objects_with_last_updated_between_date_periods() throws ParseException {
        Date before = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        Date inBetween = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        Date after = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);

        List<DatePeriod> beforeDatePeriods = Lists.newArrayList(DatePeriod.create(before, inBetween));
        List<Category> beforeCategories = d2.categoryModule().categories()
                .byLastUpdated().inDatePeriods(beforeDatePeriods).blockingGet();
        assertThat(beforeCategories.size()).isEqualTo(2);

        List<DatePeriod> afterDatePeriods = Lists.newArrayList(DatePeriod.create(inBetween, after));
        List<Category> afterCategories = d2.categoryModule().categories()
                .byLastUpdated().inDatePeriods(afterDatePeriods).blockingGet();
        assertThat(afterCategories.size()).isEqualTo(2);

        List<DatePeriod> datePeriods = Lists.newArrayList(DatePeriod.create(before, inBetween),
                DatePeriod.create(inBetween, after));
        List<Category> categories = d2.categoryModule().categories()
                .byLastUpdated().inDatePeriods(datePeriods).blockingGet();
        assertThat(categories.size()).isEqualTo(4);
    }

    @Test
    public void find_objects_with_last_updated_between_periods() throws ParseException {
        Period period = Period.builder().periodId("201710")
                .startDate(BaseIdentifiableObject.DATE_FORMAT.parse("2017-10-01T00:00:00.000"))
                .endDate(BaseIdentifiableObject.DATE_FORMAT.parse("2017-10-31T23:59:59.999"))
                .periodType(PeriodType.Monthly)
                .build();

        List<Category> beforeCategories = d2.categoryModule().categories()
                .byLastUpdated().inPeriods(Lists.newArrayList(period)).blockingGet();
        assertThat(beforeCategories.size()).isEqualTo(1);
    }
}