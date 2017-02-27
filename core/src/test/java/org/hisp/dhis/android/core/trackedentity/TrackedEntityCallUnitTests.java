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

import org.assertj.core.util.Sets;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import okhttp3.Headers;
import retrofit2.Response;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityCallUnitTests {

    @Mock
    private Cursor cursor;

    @Mock
    @SuppressWarnings("CannotMockFinalClass")
    private DatabaseAdapter database;

    @Mock
    private TrackedEntityHandler handler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private Transaction transaction;

    @Mock
    private TrackedEntityService service;

    //Mock return value of the mock service:
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<TrackedEntity>> retrofitCall;

    @Mock
    private Payload<TrackedEntity> payload;

    @Mock
    private TrackedEntity trackedEntity;

    @Mock
    private Date created, lastUpdated;

    //Captors for the service arguments:
    @Captor
    private ArgumentCaptor<Fields<TrackedEntity>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<Filter<TrackedEntity, String>> idFilterCaptor;

    @Captor
    private ArgumentCaptor<Filter<TrackedEntity, String>> lastUpdatedFilterCaptor;

    @Captor
    private ArgumentCaptor<Boolean> pagingCaptor;

    //the call we are testing:
    private TrackedEntityCall call;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(trackedEntity.uid()).thenReturn("uid1");
        when(trackedEntity.code()).thenReturn("code");
        when(trackedEntity.name()).thenReturn("name");
        when(trackedEntity.displayName()).thenReturn("display_name");
        when(trackedEntity.deleted()).thenReturn(false);
        when(trackedEntity.created()).thenReturn(created);
        when(trackedEntity.lastUpdated()).thenReturn(lastUpdated);
        when(trackedEntity.shortName()).thenReturn("short_name");
        when(trackedEntity.displayShortName()).thenReturn("display_short_name");
        when(trackedEntity.description()).thenReturn("description");
        when(trackedEntity.displayDescription()).thenReturn("display_description");

        call = new TrackedEntityCall(Sets.newLinkedHashSet(trackedEntity.uid()), database,
                handler, resourceHandler, service);

        when(database.beginNewTransaction()).thenReturn(transaction);
        when(service.trackedEntities(
                fieldsCaptor.capture(),
                idFilterCaptor.capture(),
                lastUpdatedFilterCaptor.capture(),
                pagingCaptor.capture()
        )).thenReturn(retrofitCall);
        when(retrofitCall.execute()).thenReturn(Response.success(payload));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldInvokeServer_withCorrectParameters() throws Exception {
        when(payload.items()).thenReturn(Collections.singletonList(trackedEntity));

        call.call();

        assertThat(fieldsCaptor.getValue().fields()).contains(
                TrackedEntity.uid, TrackedEntity.code, TrackedEntity.name,
                TrackedEntity.displayName, TrackedEntity.created, TrackedEntity.lastUpdated,
                TrackedEntity.shortName, TrackedEntity.displayShortName,
                TrackedEntity.description, TrackedEntity.displayDescription,
                TrackedEntity.displayDescription,
                TrackedEntity.deleted
        );
        assertThat(idFilterCaptor.getValue().values()).contains(trackedEntity.uid());
        assertThat(lastUpdatedFilterCaptor.getValue()).isNull();
        assertThat(pagingCaptor.getValue()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldInvokeServer_withCorrectParameters_withLastUpdated() throws Exception {
        String date = "2014-11-25T09:37:53.358";
        when(resourceHandler.getLastUpdated(eq(TrackedEntity.class.getSimpleName()))).thenReturn(date);
        when(payload.items()).thenReturn(Collections.singletonList(trackedEntity));

        call.call();

        assertThat(fieldsCaptor.getValue().fields()).contains(
                TrackedEntity.uid, TrackedEntity.code, TrackedEntity.name,
                TrackedEntity.displayName, TrackedEntity.created, TrackedEntity.lastUpdated,
                TrackedEntity.shortName, TrackedEntity.displayShortName,
                TrackedEntity.description, TrackedEntity.displayDescription,
                TrackedEntity.displayDescription,
                TrackedEntity.deleted
        );
        assertThat(idFilterCaptor.getValue().values()).contains(trackedEntity.uid());
        assertThat(lastUpdatedFilterCaptor.getValue().values()).contains(date);
        assertThat(pagingCaptor.getValue()).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldNotMarkTransactionSuccessful_storesOnException() throws Exception {
        when(retrofitCall.execute()).thenThrow(Exception.class);
        try {
            call.call();
            fail("expected Exception to be thrown, but isn't ");
        } catch (Exception e) {
            verify(database, times(1)).beginNewTransaction();
            verify(transaction, times(1)).end();
            verify(transaction, never()).setSuccessful();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldInsert_ifRequestSucceeds() throws Exception {
        Headers headers = new Headers.Builder().add("Date", lastUpdated.toString()).build();
        when(payload.items()).thenReturn(Collections.singletonList(trackedEntity));
        Response<Payload<TrackedEntity>> response = Response.success(payload, headers);
        when(retrofitCall.execute()).thenReturn(response);

        call.call();

        verify(database, times(1)).beginNewTransaction();
        verify(transaction, times(1)).setSuccessful();
        verify(transaction, times(1)).end();
        verify(handler, times(1)).handleTrackedEntity(eq(trackedEntity));
        //TODO: after implementing the SystemInfoCall, tests..etc modify this to actually check the date:
        //Right now it only checks if: (Date) null is an instance of Date.class, not a terribly useful:
        verify(resourceHandler, times(1)).handleResource(eq(ResourceHandler.Type.TRACKED_ENTITY), any(Date.class));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldNotFail_onEmptyInput() throws IOException {
        TrackedEntityCall call = new TrackedEntityCall(new HashSet<String>(), database,
                handler, resourceHandler, service);
        when(service.trackedEntities(
                fieldsCaptor.capture(),
                idFilterCaptor.capture(),
                lastUpdatedFilterCaptor.capture(),
                pagingCaptor.capture()
        )).thenReturn(retrofitCall);
        when(retrofitCall.execute()).thenReturn(Response.success(payload));

        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception should not be thrown.");
        } finally {
            assertThat(call.isExecuted()).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldThrowException_onConsecutiveCalls() {
        try {
            call.call();
            call.call();
            fail("Expecting an Exception on Consecutive calls");
        } catch (Exception e) {
            assertThat(IllegalStateException.class.isInstance(e)).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldMarkCallAsExecuted_onSuccess() {
        try {
            call.call();
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        } finally {
            assertThat(call.isExecuted()).isTrue();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void call_shouldMarkCallAsExecuted_onFailure() throws IOException {
        when(retrofitCall.execute()).thenThrow(new IOException());
        try {
            call.call();
            fail("IOException should be thrown");
        } catch (Exception e) {
            assertThat(call.isExecuted()).isTrue();
        }
    }
}

