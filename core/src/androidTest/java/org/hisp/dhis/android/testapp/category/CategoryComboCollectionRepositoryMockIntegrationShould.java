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

package org.hisp.dhis.android.testapp.category;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboCollectionRepository;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryComboCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    private final String BEFORE_DATE = "2007-12-24T12:24:25.203";
    private final String IN_BETWEEN_DATE = "2016-04-16T18:04:34.745";
    private final String AFTER_DATE =  "2017-12-24T12:24:25.203";

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @Test
    public void find_objects_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_children_with_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.withAllChildren().get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
        assertThat(combos.get(0).categories().isEmpty(), is(false));
    }

    @Test
    public void find_objects_with_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().eq("BIRTHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_uid() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byUid().eq("m2jTvAj5kkm");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_display_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byDisplayName().eq("Births Display");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().eq("Deaths");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().eq("DEATHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void find_objects_with_like_name() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_like_code() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_equal_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:25.203");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCreated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_equal_last_updated() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-18T16:04:34.745");
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().eq(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_last_updated_before_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(0));
    }

    @Test
    public void find_objects_with_last_updated_before_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(DEFAULT_UID));
    }

    @Test
    public void find_objects_with_last_updated_before_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().before(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_last_updated_after_date_before_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_objects_with_last_updated_after_date_in_between() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(IN_BETWEEN_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

    @Test
    public void find_objects_with_last_updated_after_date_after_both() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(AFTER_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created);
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(0));
    }

    @Test
    public void combine_date_and_string_filters() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse(BEFORE_DATE);
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byLastUpdated().after(created)
                .byName().like("%t%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(2));
    }

    @Test
    public void find_non_default_category_combo() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byIsDefault().isFalse();
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void find_default_category_combo() {
        CategoryComboCollectionRepository repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byIsDefault().isTrue();
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("p0KPaWEg3cf"));
    }

    @Test
    public void include_categories_as_children() {
        CategoryCombo categoryCombo = d2.categoryModule().categoryCombos
                .withCategories()
                .uid("m2jTvAj5kkm")
                .get();
        assertThat(categoryCombo.categories().size(), is(2));
    }

    @Test
    public void include_category_option_combos_as_children() {
        CategoryCombo categoryCombo = d2.categoryModule().categoryCombos
                .withCategories()
                .uid("m2jTvAj5kkm")
                .get();
        assertThat(categoryCombo.categories().size(), is(1));
    }
}