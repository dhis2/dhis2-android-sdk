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

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.arch.db.TableInfo;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.file.IFileReader;
import org.hisp.dhis.android.core.data.systeminfo.SystemInfoSamples;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class SystemInfoCallMockIntegrationShould extends AbsStoreTestCase {

    private MockWebServer mockWebServer;
    private Call<SystemInfo> systeminfoCall;

    private SystemInfo systemInfoFromAPI = SystemInfoSamples.get1();
    private SystemInfo systemInfoFromDB = SystemInfoSamples.get2();

    private TableInfo tableInfo = SystemInfoTableInfo.TABLE_INFO;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MockResponse mockResponse = new MockResponse();

        IFileReader fileReader = new AssetsFileReader();
        String body = fileReader.getStringFromFile("system_info.json");
        mockResponse.setBody(body);

        mockWebServer.enqueue(mockResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        SystemInfoInternalModule systemInfoInternalModule = SystemInfoInternalModule.create(databaseAdapter(), retrofit);
        systeminfoCall = systemInfoInternalModule.callFactory.create();
    }

    @Test
    public void persist_system_info_when_call() throws Exception {
        systeminfoCall.call();
        Cursor systemInfoCursor = getCursor();
        assertSystemInfoInCursor(systemInfoCursor, systemInfoFromAPI);
    }

    @Test
    public void update_system_info_when_call() throws Exception {
        database().insert(tableInfo.name(), null, systemInfoFromDB.toContentValues());
        Cursor cursorPreCall = getCursor();
        assertSystemInfoInCursor(cursorPreCall, systemInfoFromDB);

        systeminfoCall.call();
        Cursor cursorPostCall = getCursor();
        assertSystemInfoInCursor(cursorPostCall, systemInfoFromAPI);
    }

    private Cursor getCursor() {
        return database().query(tableInfo.name(), tableInfo.columns().all(),
                null, null, null, null, null);
    }

    private void assertSystemInfoInCursor(Cursor cursor, SystemInfo systemInfo) {
        assertThatCursor(cursor).hasRow(
                BaseIdentifiableObject.DATE_FORMAT.format(systemInfo.serverDate()),
                systemInfo.dateFormat(),
                systemInfo.version(),
                systemInfo.contextPath(),
                systemInfo.systemName()
        ).isExhausted();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();
        mockWebServer.shutdown();
    }
}
