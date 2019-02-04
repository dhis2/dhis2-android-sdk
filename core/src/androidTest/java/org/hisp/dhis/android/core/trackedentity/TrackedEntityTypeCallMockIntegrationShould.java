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

import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityTypeSamples;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.truth.Truth.assertThat;


@RunWith(AndroidJUnit4.class)
public class TrackedEntityTypeCallMockIntegrationShould extends AbsStoreTestCase {
    private Dhis2MockServer dhis2MockServer;

    private static final String uid = "nEenWmSyUEp";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Callable<List<TrackedEntityType>> trackedEntityTypeCall;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_types.json");
        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        HashSet<String> uids = new HashSet<>(Collections.singletonList(uid));
        APICallExecutor apiCallExecutor = APICallExecutorImpl.create(databaseAdapter());
        TrackedEntityTypeHandler handler = new TrackedEntityTypeHandler(
                TrackedEntityTypeStore.create(databaseAdapter()),
                ObjectStyleHandler.create(databaseAdapter()),
                new TrackedEntityTypeAttributeHandler(TrackedEntityTypeAttributeStore.create(databaseAdapter())));
        trackedEntityTypeCall = new TrackedEntityTypeCallFactory(getGenericCallData(d2), apiCallExecutor,
                d2.retrofit().create(TrackedEntityTypeService.class), handler).create(uids);
    }

    @Test
    public void have_valid_values_when_call() throws Exception {
        trackedEntityTypeCall.call();
        TrackedEntityType storedTrackedEntityType = TrackedEntityTypeStore.create(databaseAdapter()).selectByUid(uid);
        assertThat(storedTrackedEntityType.toBuilder().id(null).build()).isEqualTo(TrackedEntityTypeSamples.get());
    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }
}
