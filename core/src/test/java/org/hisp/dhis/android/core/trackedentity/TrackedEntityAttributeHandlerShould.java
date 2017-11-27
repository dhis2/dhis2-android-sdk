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

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.option.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeHandlerShould {

    @Mock
    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    @Mock
    private TrackedEntityAttribute trackedEntityAttribute;

    @Mock
    private OptionSet optionSet;

    // object to test
    private TrackedEntityAttributeHandler trackedEntityAttributeHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        trackedEntityAttributeHandler = new TrackedEntityAttributeHandler(trackedEntityAttributeStore);

        when(trackedEntityAttribute.uid()).thenReturn("test_tracked_entity_attribute_uid");
        when(optionSet.uid()).thenReturn("test_option_set_uid");
        when(trackedEntityAttribute.optionSet()).thenReturn(optionSet);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        trackedEntityAttributeHandler.handleTrackedEntityAttribute(null);

        // verify that store is never called
        verify(trackedEntityAttributeStore, never()).delete(anyString());

        verify(trackedEntityAttributeStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());

        verify(trackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    public void invoke_delete_when_handle_tracked_entity_attribute_set_as_deleted() throws Exception {
        when(trackedEntityAttribute.deleted()).thenReturn(Boolean.TRUE);

        trackedEntityAttributeHandler.handleTrackedEntityAttribute(trackedEntityAttribute);

        // verify that delete is called once
        verify(trackedEntityAttributeStore, times(1)).delete(anyString());

        // verify that update and insert is never called
        verify(trackedEntityAttributeStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());

        verify(trackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());

    }

    @Test
    public void invoke_only_update_when_handle_tracked_entity_attribute_inserted() throws Exception {
        when(trackedEntityAttributeStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString())).thenReturn(1);

        trackedEntityAttributeHandler.handleTrackedEntityAttribute(trackedEntityAttribute);

        // verify that update is called once
        verify(trackedEntityAttributeStore, timeout(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());

        // verify that insert and delete is never called
        verify(trackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());

        verify(trackedEntityAttributeStore, never()).delete(anyString());
    }

    @Test
    public void invoke_update_when_handle_invoke_update_and_insert_when_handle_tracked_entity_attribute_not_inserted() throws Exception {
        when(trackedEntityAttributeStore.update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString())).thenReturn(0);

        trackedEntityAttributeHandler.handleTrackedEntityAttribute(trackedEntityAttribute);

        // verify that insert is called once
        verify(trackedEntityAttributeStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());

        // verify that update is called once since we try to update before we insert
        verify(trackedEntityAttributeStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt(), anyString(), any(ValueType.class), anyString(),
                any(TrackedEntityAttributeSearchScope.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());

        // verify that delete is never called

        verify(trackedEntityAttributeStore, never()).delete(anyString());
    }
}
