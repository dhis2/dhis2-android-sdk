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

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CollectionRepositoryOneMethodMockIntegrationShould extends MockIntegrationShould {

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void get_first_object_without_filters() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .one().get();
        assertThat(combo.uid(), is(BIRTH_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_one_object() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byName().eq("Births")
                .one().get();
        assertThat(combo.uid(), is(BIRTH_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_other_object() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byIsDefault().isTrue()
                .one().get();
        assertThat(combo.uid(), is(DEFAULT_UID));
    }

    @Test
    public void get_first_when_filter_limits_to_no_objects() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .byName().eq("Wrong name")
                .one().get();
        assertThat(combo == null, is(true));
    }

    @Test
    public void get_with_all_children_returns_object_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos
                .one().getWithAllChildren();
        assertThat(combo.uid(), is(BIRTH_UID));
        assertThat(combo.categories().size(), is(2));
        assertThat(combo.categoryOptionCombos().size(), is(1));
    }
}