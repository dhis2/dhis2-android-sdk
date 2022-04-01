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

package org.hisp.dhis.android.testapp.datavalue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class DataValueCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_data_element() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byDataElementUid()
                        .eq("g9eOBujte1U")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_period() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byPeriod()
                        .eq("2018")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_organisation_unit() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byOrganisationUnitUid()
                        .eq("DiszpKrYNg8")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_category_option_combo() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byCategoryOptionComboUid()
                        .eq("Gmbgme7z9BF")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_attribute_option_combo() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byAttributeOptionComboUid()
                        .eq("bRowv6yZOF2")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_value() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byValue()
                        .eq("11")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_stored_by() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byStoredBy()
                        .eq("android")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byCreated()
                        .eq(BaseIdentifiableObject.DATE_FORMAT.parse("2010-02-11T00:00:00.000+0100"))
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byLastUpdated()
                        .eq(BaseIdentifiableObject.DATE_FORMAT.parse("2011-01-11T00:00:00.000+0000"))
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_comment() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byComment()
                        .eq("Relevant comment")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_follow_up() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byFollowUp()
                        .isFalse()
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_state() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .bySyncState().eq(State.SYNCED)
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_deleted() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byDeleted().isFalse()
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_dataset() {
        List<DataValue> dataValues =
                d2.dataValueModule().dataValues()
                        .byDataSetUid("lyLU2wR22tC")
                        .blockingGet();

        assertThat(dataValues.size()).isEqualTo(4);
    }

    @Test
    public void return_data_value_object_repository() {
        DataValueObjectRepository objectRepository = d2.dataValueModule().dataValues()
                .value("2018", "DiszpKrYNg8", "g9eOBujte1U",
                        "Gmbgme7z9BF", "bRowv6yZOF2");
        assertThat(objectRepository.blockingExists()).isEqualTo(Boolean.TRUE);
        assertThat(objectRepository.blockingGet().value()).isEqualTo("10");
    }
}