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

package org.hisp.dhis.android.core.option;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OptionSetCallShould extends BaseMockIntegrationTestEmptyEnqueable {

    private Single<List<OptionSet>> optionSetCall;
    private D2CallExecutor d2CallExecutor;

    @Before
    public void setUp() throws D2Error {
        dhis2MockServer.enqueueMockResponse("option/option_sets.json");
        Set<String> uids = new HashSet<>();
        uids.add("POc7DkGU3QU");

        optionSetCall = objects.d2DIComponent.optionSetCall().download(uids);

        d2CallExecutor = D2CallExecutor.create(databaseAdapter);
    }

    @Test
    public void persist_option_sets_in_data_base_when_call() throws Exception {
        executeOptionSetCall();

        OptionSetCollectionRepository optionSets = d2.optionModule().optionSets();
        assertThat(optionSets.blockingCount()).isEqualTo(2);
        assertThat(optionSets.uid("VQ2lai3OfVG").blockingExists()).isTrue();
        assertThat(optionSets.uid("TQ2lai3OfVG").blockingExists()).isTrue();
    }

    @Test
    public void return_option_set_after_call() throws Exception {
        List<OptionSet> optionSetList = executeOptionSetCall();

        assertThat(optionSetList.size()).isEqualTo(2);

        OptionSet optionSet = optionSetList.get(0);

        assertThat(optionSet.uid()).isEqualTo("VQ2lai3OfVG");
        assertThat(optionSet.code()).isNull();
        assertThat(optionSet.name()).isEqualTo("Age category");
        assertThat(optionSet.displayName()).isEqualTo("Age category");
        assertThat(optionSet.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-06-22T10:59:26.564"));
        assertThat(optionSet.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-06T14:23:38.789"));
        assertThat(optionSet.version()).isEqualTo(1);
        assertThat(optionSet.valueType()).isEqualTo(ValueType.TEXT);
    }

    private List<OptionSet> executeOptionSetCall() throws Exception{

        return d2CallExecutor.executeD2CallTransactionally(() -> {
            List<OptionSet> optionSets = null;
            try {
                optionSets = optionSetCall.blockingGet();
            } catch (Exception ignored) {
            }

            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors();
            return optionSets;
        });
    }
}
