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
package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class SystemInfoCallMockIntegrationShould extends AbsStoreTestCase {
    private static final String[] SYSTEM_INFO_PROJECTION = {
            SystemInfoModel.Columns.SERVER_DATE,
            SystemInfoModel.Columns.DATE_FORMAT,
            SystemInfoModel.Columns.VERSION,
            SystemInfoModel.Columns.CONTEXT_PATH
    };

    private MockWebServer mockWebServer;
    private Call<Response<SystemInfo>> systeminfoCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody("{\n" +
                "\n" +
                "    \"contextPath\": \"https://play.dhis2.org/dev\",\n" +
                "    \"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:51.0) Gecko/20100101 Firefox/51.0\",\n" +
                "    \"calendar\": \"iso8601\",\n" +
                "    \"dateFormat\": \"yyyy-mm-dd\",\n" +
                "    \"serverDate\": \"2017-02-27T14:55:45.808\",\n" +
                "    \"lastAnalyticsTableSuccess\": \"2017-01-26T23:19:34.009\",\n" +
                "    \"intervalSinceLastAnalyticsTableSuccess\": \"759 h, 36 m, 11 s\",\n" +
                "    \"lastAnalyticsTableRuntime\": \"5 m, 17 s\",\n" +
                "    \"version\": \"2.27-SNAPSHOT\",\n" +
                "    \"revision\": \"0223dac\",\n" +
                "    \"buildTime\": \"2017-02-27T11:32:16.000\",\n" +
                "    \"jasperReportsVersion\": \"6.3.1\",\n" +
                "    \"environmentVariable\": \"DHIS2_HOME\",\n" +
                "    \"databaseInfo\": {\n" +
                "        \"type\": \"PostgreSQL\",\n" +
                "        \"spatialSupport\": true\n" +
                "    },\n" +
                "    \"encryption\": false,\n" +
                "    \"systemId\": \"eed3d451-4ff5-4193-b951-ffcc68954299\",\n" +
                "    \"systemName\": \"DHIS 2 Demo - Sierra Leone\",\n" +
                "    \"systemMetadataVersion\": \"Version_4\",\n" +
                "    \"isMetadataVersionEnabled\": true,\n" +
                "    \"isMetadataSyncEnabled\": false\n" +
                "\n" +
                "}");

        mockWebServer.enqueue(mockResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FieldsConverterFactory.create())
                .build();


        SystemInfoService systemInfoService = retrofit.create(SystemInfoService.class);
        SystemInfoStore systemInfoStore = new SystemInfoStoreImpl(databaseAdapter());
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());

        SystemInfoQuery systemInfoQuery = SystemInfoQuery.defaultQuery(DEFAULT_IS_TRANSLATION_ON,
                DEFAULT_TRANSLATION_LOCALE);

        systeminfoCall = new SystemInfoCall(
                databaseAdapter(), systemInfoStore, systemInfoService, resourceStore,
                systemInfoQuery
        );
    }

    @Test
    @MediumTest
    public void persist_system_info_when_call() throws Exception {
        // fake call to api to retrieve response
        systeminfoCall.call();

        Cursor systemInfoCursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION,
                null, null, null, null, null);

        assertThatCursor(systemInfoCursor).hasRow(
                "2017-02-27T14:55:45.808",
                "yyyy-mm-dd",
                "2.27-SNAPSHOT",
                "https://play.dhis2.org/dev"
        ).isExhausted();
    }

    @Test
    @MediumTest
    public void update_system_info_when_call() throws Exception {
        ContentValues systemInfo = new ContentValues();
        systemInfo.put(SystemInfoModel.Columns.SERVER_DATE, "2017-02-27T15:00:46.332");
        systemInfo.put(SystemInfoModel.Columns.VERSION, "2.26");
        systemInfo.put(SystemInfoModel.Columns.CONTEXT_PATH, "https://play.dhis2.org/dev");
        systemInfo.put(SystemInfoModel.Columns.DATE_FORMAT, "yyyy-mm-dd:hh:MM:dd");

        // inserting system info into database
        database().insert(SystemInfoModel.TABLE, null, systemInfo);

        Cursor systemInfoCursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION,
                null, null, null, null, null);
        // checking that it was successfully inserted
        assertThatCursor(systemInfoCursor).hasRow(
                "2017-02-27T15:00:46.332",
                "yyyy-mm-dd:hh:MM:dd",
                "2.26",
                "https://play.dhis2.org/dev"
        ).isExhausted();


        // fake call to api to retrieve response
        systeminfoCall.call();

        systemInfoCursor = database().query(SystemInfoModel.TABLE, SYSTEM_INFO_PROJECTION,
                null, null, null, null, null);

        // check that systemInfo is updated

        assertThatCursor(systemInfoCursor).hasRow(
                "2017-02-27T14:55:45.808",
                "yyyy-mm-dd",
                "2.27-SNAPSHOT",
                "https://play.dhis2.org/dev"
        ).isExhausted();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        mockWebServer.shutdown();
    }
}
