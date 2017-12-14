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
package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.utils.HeaderUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityCallMockIntegrationShould extends AbsStoreTestCase {
    public static final String[] PROJECTION = {
            TrackedEntityModel.Columns.UID,
            TrackedEntityModel.Columns.CODE,
            TrackedEntityModel.Columns.NAME,
            TrackedEntityModel.Columns.DISPLAY_NAME,
            TrackedEntityModel.Columns.CREATED,
            TrackedEntityModel.Columns.LAST_UPDATED,
            TrackedEntityModel.Columns.SHORT_NAME,
            TrackedEntityModel.Columns.DISPLAY_SHORT_NAME,
            TrackedEntityModel.Columns.DESCRIPTION,
            TrackedEntityModel.Columns.DISPLAY_DESCRIPTION,
    };
/*    private static String[] RESOURCE_PROJECTION = {
            ResourceModel.Columns.RESOURCE_TYPE,
            ResourceModel.Columns.LAST_SYNCED
    };*/

    private MockWebServer server;

    private Call<Response<Payload<TrackedEntity>>> trackedEntityCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        server = new MockWebServer();
        server.start();

        MockResponse response = new MockResponse();
        response.setHeader(HeaderUtils.DATE, "Tue, 21 Feb 2017 15:44:46 GMT");
        response.setResponseCode(200);
        response.setBody("{\n" +
                "  \"trackedEntities\": [\n" +
                "  {\n" +
                "    \"lastUpdated\": \"2014-04-14T13:54:54.497\",\n" +
                "    \"created\": \"2014-04-14T13:54:54.497\",\n" +
                "    \"name\": \"Lab sample\",\n" +
                "    \"id\": \"kIeke8tAQnd\",\n" +
                "    \"displayDescription\": \"Lab sample\",\n" +
                "    \"displayName\": \"Lab sample\",\n" +
                "    \"description\": \"Lab sample\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"lastUpdated\": \"2015-10-14T13:36:53.063\",\n" +
                "    \"created\": \"2014-08-20T12:28:56.409\",\n" +
                "    \"name\": \"Person\",\n" +
                "    \"id\": \"nEenWmSyUEp\",\n" +
                "    \"displayDescription\": \"Person\",\n" +
                "    \"displayName\": \"Person\",\n" +
                "    \"description\": \"Person\"\n" +
                "  }\n" +
                " ]\n" +
                "}");
        server.enqueue(response);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/")) // ??
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FieldsConverterFactory.create())
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TrackedEntityService service = retrofit.create(TrackedEntityService.class);

        HashSet<String> uids = new HashSet<>(Arrays.asList("kIeke8tAQnd", "nEenWmSyUEp"));
        TrackedEntityStore trackedEntityStore = new TrackedEntityStoreImpl(databaseAdapter());
        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());

        trackedEntityCall = new TrackedEntityCall(
                uids, databaseAdapter(), trackedEntityStore, resourceStore, service, new Date()
        );
    }

    @Test
    public void have_valid_values_when_call() throws Exception {
        trackedEntityCall.call();

        Cursor cursor = database().query(TrackedEntityModel.TABLE, PROJECTION, null, null, null, null, null);
      /*  Cursor resourceCursor = database().query(ResourceModel.TABLE,
                RESOURCE_PROJECTION, null, null, null, null, null);
*/
        assertThatCursor(cursor).hasRow("kIeke8tAQnd", null, "Lab sample", "Lab sample", "2014-04-14T13:54:54.497",
                "2014-04-14T13:54:54.497", null, null, "Lab sample", "Lab sample");

        assertThatCursor(cursor).hasRow("nEenWmSyUEp", null, "Person", "Person", "2014-08-20T12:28:56.409",
                "2015-10-14T13:36:53.063", null, null, "Person", "Person").isExhausted();

        //TODO: make sure this date is correctly formated:
        //assertThatCursor(resourceCursor).hasRow(OrganisationUnit.class.getSimpleName(), "2017-02-21T16:44:46.000");
    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        server.shutdown();
    }
}
