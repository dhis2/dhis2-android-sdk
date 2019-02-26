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

package org.hisp.dhis.android.testapp.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class DataElementCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void find_all() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .get();
        assertThat(dataElements.size(), is(4));
    }

    @Test
    public void filter_by_value_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byValueType().eq(ValueType.TEXT)
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_zero_is_significant() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byZeroIsSignificant().isFalse()
                .get();
        assertThat(dataElements.size(), is(3));
    }

    @Test
    public void filter_by_aggregation_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byAggregationType().eq("AVERAGE")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_form_name() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byFormName().eq("ANC Visit")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_domain_type() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byDomainType().eq("TRACKER")
                .get();
        assertThat(dataElements.size(), is(4));
    }

    @Test
    public void filter_by_display_form_name() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byDisplayFormName().eq("ANC Visit")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_option_set() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byOptionSetUid().eq("VQ2lai3OfVG")
                .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void filter_by_category_combo() {
        List<DataElement> dataElements = d2.dataElementModule().dataElements
                .byCategoryComboUid().eq("m2jTvAj5kkm")
                .get();
        assertThat(dataElements.size(), is(1));
    }
}