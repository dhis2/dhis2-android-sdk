/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class OptionSetCallShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private Call<List<OptionSet>> optionSetCall;
    private D2CallExecutor d2CallExecutor;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        String response = "{\n" +
                "\n" +
                "    \"pager\": {\n" +
                "        \"page\": 1,\n" +
                "        \"pageCount\": 1,\n" +
                "        \"total\": 1,\n" +
                "        \"pageSize\": 50\n" +
                "    },\n" +
                "    \"optionSets\": [\n" +
                "        {\n" +
                "            \"lastUpdated\": \"2012-09-20T16:05:17.555\",\n" +
                "            \"created\": \"2012-09-20T16:05:17.555\",\n" +
                "            \"name\": \"MNCH PMTCT code\",\n" +
                "            \"id\": \"POc7DkGU3QU\",\n" +
                "            \"displayName\": \"MNCH PMTCT code\",\n" +
                "            \"valueType\": \"TEXT\",\n" +
                "            \"version\": 1,\n" +
                "            \"options\": [\n" +
                "                {\n" +
                "                    \"code\": \"C\",\n" +
                "                    \"created\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"lastUpdated\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"name\": \"C\",\n" +
                "                    \"id\": \"s2gIL3CEyKL\",\n" +
                "                    \"displayName\": \"C\",\n" +
                "                    \"externalAccess\": false,\n" +
                "                    \"sortOrder\": 1,\n" +
                "                    \"optionSet\": {\n" +
                "                        \"id\": \"POc7DkGU3QU\"\n" +
                "                    },\n" +
                "                    \"userGroupAccesses\": [ ],\n" +
                "                    \"attributeValues\": [ ],\n" +
                "                    \"translations\": [ ],\n" +
                "                    \"userAccesses\": [ ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"code\": \"TR\",\n" +
                "                    \"created\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"lastUpdated\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"name\": \"TR\",\n" +
                "                    \"id\": \"poM80hUlVi9\",\n" +
                "                    \"displayName\": \"TR\",\n" +
                "                    \"externalAccess\": false,\n" +
                "                    \"sortOrder\": 2,\n" +
                "                    \"optionSet\": {\n" +
                "                        \"id\": \"POc7DkGU3QU\"\n" +
                "                    },\n" +
                "                    \"userGroupAccesses\": [ ],\n" +
                "                    \"attributeValues\": [ ],\n" +
                "                    \"translations\": [ ],\n" +
                "                    \"userAccesses\": [ ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"code\": \"TRR\",\n" +
                "                    \"created\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"lastUpdated\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"name\": \"TRR\",\n" +
                "                    \"id\": \"hcvPpAy3kb2\",\n" +
                "                    \"displayName\": \"TRR\",\n" +
                "                    \"externalAccess\": false,\n" +
                "                    \"sortOrder\": 3,\n" +
                "                    \"optionSet\": {\n" +
                "                        \"id\": \"POc7DkGU3QU\"\n" +
                "                    },\n" +
                "                    \"userGroupAccesses\": [ ],\n" +
                "                    \"attributeValues\": [ ],\n" +
                "                    \"translations\": [ ],\n" +
                "                    \"userAccesses\": [ ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"code\": \"TRRDm\",\n" +
                "                    \"created\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"lastUpdated\": \"2014-08-18T12:39:16.000\",\n" +
                "                    \"name\": \"TRRDm\",\n" +
                "                    \"id\": \"u4wsy7OPQIg\",\n" +
                "                    \"displayName\": \"TRRDm\",\n" +
                "                    \"externalAccess\": false,\n" +
                "                    \"sortOrder\": 4,\n" +
                "                    \"optionSet\": {\n" +
                "                        \"id\": \"POc7DkGU3QU\"\n" +
                "                    },\n" +
                "                    \"userGroupAccesses\": [ ],\n" +
                "                    \"attributeValues\": [ ],\n" +
                "                    \"translations\": [ ],\n" +
                "                    \"userAccesses\": [ ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "}";

        dhis2MockServer.enqueueMockResponse(200, response);

        // ToDo: consider moving this out
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Set<String> uids = new HashSet<>();
        uids.add("POc7DkGU3QU");

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
        optionSetCall = OptionSetCall.factory(getGenericCallData(d2).retrofit()).create(getGenericCallData(d2), uids);

        d2CallExecutor = new D2CallExecutor();

    }

    @Test
    public void persist_option_set_with_options_in_data_base_when_call() throws Exception {
        d2CallExecutor.executeD2Call(optionSetCall);

        Cursor optionSetCursor = database().query(OptionSetTableInfo.TABLE_INFO.name(),
                ColumnsArrayUtils.getColumnsWithId(OptionSetTableInfo.TABLE_INFO.columns().all()),
                null, null, null,null, null);
        Cursor optionCursor = database().query(OptionTableInfo.TABLE_INFO.name(),
                ColumnsArrayUtils.getColumnsWithId(OptionTableInfo.TABLE_INFO.columns().all()),
                null, null, null, null, null);

        assertThatCursor(optionSetCursor)
                .hasRow(
                        1L, // id
                        "POc7DkGU3QU", // uid
                        null, // code
                        "MNCH PMTCT code", // name
                        "MNCH PMTCT code", // displayName
                        "2012-09-20T16:05:17.555", // created
                        "2012-09-20T16:05:17.555", // lastUpdated
                        1, // version
                        "TEXT" // valueType
                ).isExhausted();

        assertThatCursor(optionCursor)
                .hasRow(
                        1L, // id
                        "s2gIL3CEyKL", // uid
                        "C", // code
                        "C", // name
                        "C", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        "1", // sortOrder
                        "POc7DkGU3QU"  // optionSet
                );

        assertThatCursor(optionCursor)
                .hasRow(
                        2L, // id
                        "poM80hUlVi9", // uid
                        "TR", // code
                        "TR", // name
                        "TR", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        "2", // sortOrder
                        "POc7DkGU3QU"  // optionSet
                );


        assertThatCursor(optionCursor)
                .hasRow(
                        3L, // id
                        "hcvPpAy3kb2", // uid
                        "TRR", // code
                        "TRR", // name
                        "TRR", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        "3", // sortOrder
                        "POc7DkGU3QU"  // optionSet
                );

        assertThatCursor(optionCursor)
                .hasRow(
                        4L, // id
                        "u4wsy7OPQIg", // uid
                        "TRRDm", // code
                        "TRRDm", // name
                        "TRRDm", // displayName
                        "2014-08-18T12:39:16.000", // created
                        "2014-08-18T12:39:16.000", // lastUpdated
                        "4", // sortOrder
                        "POc7DkGU3QU"  // optionSet
                )
                .isExhausted();

    }

    @Test
    public void return_option_set_after_call() throws Exception {
        List<OptionSet> optionSetList = d2CallExecutor.executeD2Call(optionSetCall);

        assertThat(optionSetList.size()).isEqualTo(1);

        OptionSet optionSet = optionSetList.get(0);

        assertThat(optionSet.uid()).isEqualTo("POc7DkGU3QU");
        assertThat(optionSet.code()).isNull();
        assertThat(optionSet.name()).isEqualTo("MNCH PMTCT code");
        assertThat(optionSet.displayName()).isEqualTo("MNCH PMTCT code");
        assertThat(optionSet.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-09-20T16:05:17.555"));
        assertThat(optionSet.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-09-20T16:05:17.555"));
        assertThat(optionSet.version()).isEqualTo(1);
        assertThat(optionSet.valueType()).isEqualTo(ValueType.TEXT);

        assertThat(optionSet.options().size()).isEqualTo(4);


    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }
}
