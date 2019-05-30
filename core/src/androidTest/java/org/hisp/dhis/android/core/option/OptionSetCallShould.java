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

package org.hisp.dhis.android.core.option;

import android.database.Cursor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OptionSetCallShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private Callable<List<OptionSet>> optionSetCall;
    private D2CallExecutor d2CallExecutor;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer();
        dhis2MockServer.enqueueMockResponse("option/option_sets.json");

        // ToDo: consider moving this out
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Set<String> uids = new HashSet<>();
        uids.add("POc7DkGU3QU");

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        optionSetCall = getD2DIComponent(d2).optionSetCallFactory().create(uids);

        d2CallExecutor = D2CallExecutor.create(databaseAdapter());

    }

    @Test
    public void persist_option_set_with_options_in_data_base_when_call() throws Exception {
        executeOptionSetCall();

        Cursor optionSetCursor = database().query(OptionSetTableInfo.TABLE_INFO.name(),
                ColumnsArrayUtils.getColumnsWithId(OptionSetTableInfo.TABLE_INFO.columns().all()),
                null, null, null,null, null);
        Cursor optionCursor = database().query(OptionTableInfo.TABLE_INFO.name(),
                ColumnsArrayUtils.getColumnsWithId(OptionTableInfo.TABLE_INFO.columns().all()),
                null, null, null, null, null);

        assertThatCursor(optionSetCursor)
                .hasRow(
                        1L, // id
                        "VQ2lai3OfVG", // uid
                        null, // code
                        "Age category", // name
                        "Age category", // displayName
                        "2014-06-22T10:59:26.564", // created
                        "2015-08-06T14:23:38.789", // lastUpdated
                        1, // version
                        "TEXT" // valueType
                ).hasRow(
                        2L, // id
                        "TQ2lai3OfVG", // uid
                        null, // code
                        "One option", // name
                        "One option", // displayName
                        "2014-06-22T10:59:26.564", // created
                        "2015-08-06T14:23:38.789", // lastUpdated
                        2, // version
                        "NUMBER" // valueType
                ).isExhausted();

        assertThatCursor(optionCursor)
                .hasRow(
                        1L, // id
                        "Y1ILwhy5VDY", // uid
                        "0-14 years", // code
                        "0-14 years", // name
                        "0-14 years", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        1, // sortOrder
                        "VQ2lai3OfVG"  // optionSet
                ).hasRow(
                        2L, // id
                        "egT1YqFWsVk", // uid
                        "15-19 years", // code
                        "15-19 years", // name
                        "15-19 years", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        2, // sortOrder
                        "VQ2lai3OfVG"  // optionSet
                ).hasRow(
                        4L, // id
                        "Z1ILwhy5VDY", // uid
                        "First option", // code
                        "First option", // name
                        "First option", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        1, // sortOrder
                        "TQ2lai3OfVG"  // optionSet
                ).isExhausted();

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

        assertThat(optionSet.options().size()).isEqualTo(3);
    }

    private List<OptionSet> executeOptionSetCall() throws Exception{

        return d2CallExecutor.executeD2CallTransactionally(() -> {
            List<OptionSet> optionSets = null;
            try {
                optionSets = optionSetCall.call();
            } catch (Exception ignored) {
            }

            ForeignKeyCleanerImpl.create(databaseAdapter()).cleanForeignKeyErrors();
            return optionSets;
        });
    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }
}
