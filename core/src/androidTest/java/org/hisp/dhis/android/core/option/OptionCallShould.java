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
public class OptionCallShould extends BaseMockIntegrationTestEmptyEnqueable {

    private Single<List<Option>> optionCall;
    private D2CallExecutor d2CallExecutor;

    @Before
    public void setUp() throws Exception {
        dhis2MockServer.enqueueMockResponse("option/option_sets.json");
        dhis2MockServer.enqueueMockResponse("option/options.json");
        Set<String> uids = new HashSet<>();

        uids.add("Y1ILwhy5VDY");
        uids.add("egT1YqFWsVk");
        uids.add("non_existent_option_uid");
        uids.add("Z1ILwhy5VDY");

        optionCall = objects.d2DIComponent.optionCall().download(uids);

        d2CallExecutor = D2CallExecutor.create(databaseAdapter);

        executeOptionSetCall();
    }

    @Test
    public void persist_options_in_data_base_when_call() throws Exception {
        executeOptionCall();

        OptionCollectionRepository options = d2.optionModule().options();
        assertThat(options.blockingCount()).isEqualTo(3);
        assertThat(options.uid("Y1ILwhy5VDY").blockingExists()).isTrue();
        assertThat(options.uid("egT1YqFWsVk").blockingExists()).isTrue();
        assertThat(options.uid("non_existent_option_uid").blockingExists()).isFalse();
        assertThat(options.uid("Z1ILwhy5VDY").blockingExists()).isTrue();
    }

    @Test
    public void return_options_after_call() throws Exception {
        List<Option> optionList = executeOptionCall();

        assertThat(optionList.size()).isEqualTo(4);

        Option option = optionList.get(0);

        assertThat(option.uid()).isEqualTo("Y1ILwhy5VDY");
        assertThat(option.code()).isEqualTo("0-14 years");
        assertThat(option.name()).isEqualTo("0-14 years");
        assertThat(option.displayName()).isEqualTo("0-14 years");
        assertThat(option.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-18T12:39:16.000"));
        assertThat(option.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-18T12:39:16.000"));
        assertThat(option.optionSet().uid()).isEqualTo("VQ2lai3OfVG");
        assertThat(option.sortOrder()).isEqualTo(1);
    }

    private void executeOptionSetCall() throws Exception {
        d2CallExecutor.executeD2CallTransactionally(() -> {
            List<OptionSet> optionSets = null;
            try {
                Set<String> uids = new HashSet<>();
                uids.add("POc7DkGU3QU");

                optionSets = objects.d2DIComponent.optionSetCall().download(uids).blockingGet();
            } catch (Exception ignored) {
            }

            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors();
            return optionSets;
        });
    }

    private List<Option> executeOptionCall() throws Exception{

        return d2CallExecutor.executeD2CallTransactionally(() -> {
            List<Option> options = null;
            try {
                options = optionCall.blockingGet();
            } catch (Exception ignored) {
            }

            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors();
            return options;
        });
    }
}
