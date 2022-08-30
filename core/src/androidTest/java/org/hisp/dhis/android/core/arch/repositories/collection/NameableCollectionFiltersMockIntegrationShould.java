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

import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class NameableCollectionFiltersMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_filter_by_short_uid() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byUid().eq("as6ygGvUGNg")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_code() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byCode().eq("default code")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byName().eq("default name")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_display_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byDisplayName().eq("default display name")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_created() throws ParseException {
        Date created = BaseIdentifiableObject.DATE_FORMAT.parse("2011-12-24T12:24:24.777");
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byCreated().eq(created)
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_last_updated() throws ParseException {
        Date lastUpdated = BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-12T20:37:48.666");
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byLastUpdated().eq(lastUpdated)
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_short_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byShortName().eq("default short name")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_display_short_name() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byDisplayShortName().eq("default display short name")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_description() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byDescription().eq("default description")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_filter_by_display_description() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byDisplayDescription().eq("default display description")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_combination_of_identifiable_and_nameable_filter() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byName().eq("default name")
                .byDisplayDescription().eq("default display description")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }

    @Test
    public void allow_combination_of_nameable_and_identifiable_filter() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions()
                .byDisplayDescription().eq("default display description")
                .byName().eq("default name")
                .blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(1);
    }
}