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

package org.hisp.dhis.android.testapp.datavalue;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class DataValueCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
        downloadAggregatedData();
    }

    @Test
    public void find_all() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .get();

        assertThat(dataValues.size(), is(3));
    }

    @Test
    public void filter_by_data_element() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byDataElementUid()
                        .eq("g9eOBujte1U")
                        .get();

        assertThat(dataValues.size(), is(3));
    }

    @Test
    public void filter_by_period() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byPeriod()
                        .eq("201809")
                        .get();

        assertThat(dataValues.size(), is(1));
    }

    @Test
    public void filter_by_organisation_unit() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byOrganisationUnitUid()
                        .eq("DiszpKrYNg8")
                        .get();

        assertThat(dataValues.size(), is(3));
    }

    @Test
    public void filter_by_category_option_combo() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byCategoryOptionComboUid()
                        .eq("Gmbgme7z9BF")
                        .get();

        assertThat(dataValues.size(), is(3));
    }

    @Test
    public void filter_by_attribute_option_combo() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byAttributeOptionComboUid()
                        .eq("bRowv6yZOF2")
                        .get();

        assertThat(dataValues.size(), is(3));
    }

    @Test
    public void filter_by_value() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byValue()
                        .eq("11")
                        .get();

        assertThat(dataValues.size(), is(1));
    }

    @Test
    public void filter_by_stored_by() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byStoredBy()
                        .eq("android")
                        .get();

        assertThat(dataValues.size(), is(2));
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byCreated()
                        .eq(BaseIdentifiableObject.DATE_FORMAT.parse("2010-01-11T00:00:00.000+0000"))
                        .get();

        assertThat(dataValues.size(), is(1));
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byLastUpdated()
                        .eq(BaseIdentifiableObject.DATE_FORMAT.parse("2011-01-11T00:00:00.000+0000"))
                        .get();

        assertThat(dataValues.size(), is(1));
    }

    @Test
    public void filter_by_comment() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byComment()
                        .eq("Relevant comment")
                        .get();

        assertThat(dataValues.size(), is(1));
    }

    @Test
    public void filter_by_follow_up() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues
                        .byFollowUp()
                        .isFalse()
                        .get();

        assertThat(dataValues.size(), is(2));
    }

}
