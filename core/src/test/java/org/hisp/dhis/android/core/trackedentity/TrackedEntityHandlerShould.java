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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityHandlerShould {
    public static final String UID = "uid1";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "display_name";
    public static final boolean DELETED = false;
    public static final String SHORT_NAME = "short_name";
    public static final String DISPLAY_SHORT_NAME = "display_short_name";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_DESCRIPTION = "display_description";
    @Mock
    private TrackedEntityStore store;

    @Mock
    private TrackedEntity trackedEntity;

    @Mock
    private Date created, lastUpdated;

    private TrackedEntityHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(trackedEntity.uid()).thenReturn(UID);
        when(trackedEntity.code()).thenReturn(CODE);
        when(trackedEntity.name()).thenReturn(NAME);
        when(trackedEntity.displayName()).thenReturn(DISPLAY_NAME);
        when(trackedEntity.deleted()).thenReturn(DELETED);
        when(trackedEntity.created()).thenReturn(created);
        when(trackedEntity.lastUpdated()).thenReturn(lastUpdated);
        when(trackedEntity.shortName()).thenReturn(SHORT_NAME);
        when(trackedEntity.displayShortName()).thenReturn(DISPLAY_SHORT_NAME);
        when(trackedEntity.description()).thenReturn(DESCRIPTION);
        when(trackedEntity.displayDescription()).thenReturn(DISPLAY_DESCRIPTION);

        handler = new TrackedEntityHandler(store);
    }

    @Test
    public void invoke_delete_when_handle_tracked_entity_set_as_deleted() {
        when(trackedEntity.deleted()).thenReturn(!DELETED);
        handler.handleTrackedEntity(trackedEntity);
        verify(store, times(1)).delete(UID);
    }

    @Test
    public void invoke_only_update_when_handle_tracked_entity_inserted() {
        when(store.update(anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(1);

        handler.handleTrackedEntity(trackedEntity);

        verify(store, times(1)).update(eq(UID), eq(CODE), eq(NAME), eq(DISPLAY_NAME), eq(created), eq(lastUpdated),
                eq(SHORT_NAME), eq(DISPLAY_SHORT_NAME), eq(DESCRIPTION), eq(DISPLAY_DESCRIPTION), eq(UID));
    }

    @Test
    public void invoke_insert_when_handle_tracked_entity_not_inserted_before() {
        when(store.update(anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString())
        ).thenReturn(-1);

        handler.handleTrackedEntity(trackedEntity);

        verify(store, times(1)).insert(eq(UID), eq(CODE), eq(NAME), eq(DISPLAY_NAME), eq(created), eq(lastUpdated),
                eq(SHORT_NAME), eq(DISPLAY_SHORT_NAME), eq(DESCRIPTION), eq(DISPLAY_DESCRIPTION));
    }

    @Test (expected = NullPointerException.class)
    public void thrown_null_pointer_exception_when_pass_null_on_handle_tracked_entity_method() {
        handler.handleTrackedEntity(null);
    }
}
