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

package org.hisp.dhis.android.testapp.dataset;

import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class SectionCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all_objects() {
        List<Section> sections = d2.dataSetModule().sections().blockingGet();
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0).name()).isEqualTo("Immunization");
    }

    @Test
    public void filter_by_description() {
        List<Section> sections = d2.dataSetModule().sections()
                .byDescription().eq("Immunization dose administration")
                .blockingGet();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_sort_order() {
        List<Section> sections = d2.dataSetModule().sections()
                .bySortOrder().eq(1)
                .blockingGet();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_show_row_totals() {
        List<Section> sections = d2.dataSetModule().sections()
                .byShowRowTotals().isTrue()
                .blockingGet();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_show_column_totals() {
        List<Section> sections = d2.dataSetModule().sections()
                .byShowColumnTotals().isFalse()
                .blockingGet();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_data_set_uid() {
        List<Section> sections = d2.dataSetModule().sections()
                .byDataSetUid().eq("lyLU2wR22tC")
                .blockingGet();
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    public void return_greyed_fields_as_children() {
        Section section = d2.dataSetModule().sections()
                .withGreyedFields().one().blockingGet();
        assertThat(section.greyedFields().size()).isEqualTo(1);
        assertThat(section.greyedFields().get(0).uid()).isEqualTo("ca8lfO062zg.Prlt0C1RF0s");
    }

    @Test
    public void return_data_element_as_children() {
        Section section = d2.dataSetModule().sections()
                .withDataElements().one().blockingGet();
        assertThat(section.dataElements().size()).isEqualTo(1);
        assertThat(section.dataElements().get(0).uid()).isEqualTo("g9eOBujte1U");
        assertThat(section.dataElements().get(0).code()).isEqualTo("DE_2005735");
    }

    @Test
    public void return_indicators_as_children() {
        Section section = d2.dataSetModule().sections()
                .withIndicators().one().blockingGet();
        assertThat(section.indicators().size()).isEqualTo(1);
        assertThat(section.indicators().get(0).uid()).isEqualTo("ReUHfIn0pTQ");
        assertThat(section.indicators().get(0).code()).isEqualTo("IN_52462");
    }
}