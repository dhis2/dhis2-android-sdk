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

package org.hisp.dhis.android.testapp.dataset;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.dataset.Section;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SectionCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all_objects() {
        List<Section> sections = d2.dataSetModule().sections.get();
        assertThat(sections.size(), is(1));
        assertThat(sections.get(0).name(), is("Immunization"));
    }

    @Test
    public void filter_by_description() {
        List<Section> sections = d2.dataSetModule().sections
                .byDescription().eq("Immunization dose administration")
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void filter_by_sort_order() {
        List<Section> sections = d2.dataSetModule().sections
                .bySortOrder().eq(1)
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void filter_by_show_row_totals() {
        List<Section> sections = d2.dataSetModule().sections
                .byShowRowTotals().isTrue()
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void filter_by_show_column_totals() {
        List<Section> sections = d2.dataSetModule().sections
                .byShowColumnTotals().isFalse()
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void filter_by_data_set_uid() {
        List<Section> sections = d2.dataSetModule().sections
                .byDataSetUid().eq("lyLU2wR22tC")
                .get();
        assertThat(sections.size(), is(1));
    }

    @Test
    public void return_greyed_fields_as_children() {
        Section section = d2.dataSetModule().sections
                .one().getWithAllChildren();
        assertThat(section.greyedFields().size(), is(1));
        assertThat(section.greyedFields().get(0).uid(), is("ca8lfO062zg.Prlt0C1RF0s"));
    }

    @Test
    public void return_data_element_as_children() {
        Section section = d2.dataSetModule().sections
                .one().getWithAllChildren();
        assertThat(section.dataElements().size(), is(1));
        assertThat(section.dataElements().get(0).uid(), is("g9eOBujte1U"));
        assertThat(section.dataElements().get(0).code(), is("DE_2005735"));
    }
}