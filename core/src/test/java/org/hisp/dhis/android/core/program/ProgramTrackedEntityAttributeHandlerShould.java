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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramTrackedEntityAttributeHandlerShould {

    @Mock
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    @Mock
    private TrackedEntityAttributeHandler trackedEntityAttributeHandler;

    @Mock
    private ProgramTrackedEntityAttribute programTrackedEntityAttribute;

    @Mock
    private Program program;

    @Mock
    private TrackedEntityAttribute trackedEntityAttribute;

    // object to test
    private ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler;

    // list of program tracked entity attributes
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programTrackedEntityAttributeHandler = new ProgramTrackedEntityAttributeHandler(
                programTrackedEntityAttributeStore, trackedEntityAttributeHandler
        );

        when(programTrackedEntityAttribute.uid()).thenReturn("test_program_tracked_entity_attribute_uid");
        when(program.uid()).thenReturn("test_program_uid");
        when(trackedEntityAttribute.uid()).thenReturn("test_tracked_entity_attribute_uid");

        when(programTrackedEntityAttribute.program()).thenReturn(program);
        when(programTrackedEntityAttribute.trackedEntityAttribute()).thenReturn(trackedEntityAttribute);

        programTrackedEntityAttributes = new ArrayList<>();
        programTrackedEntityAttributes.add(programTrackedEntityAttribute);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        programTrackedEntityAttributeHandler.handleProgramTrackedEntityAttributes(null);

        // verify that store is never called
        verify(programTrackedEntityAttributeStore, never()).delete(anyString());
        verify(programTrackedEntityAttributeStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt(), anyString());
        verify(programTrackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt());

        // verify that tracked entity attribute handler is never called
        verify(trackedEntityAttributeHandler, never()).handleTrackedEntityAttribute(any(TrackedEntityAttribute.class));

    }

    @Test
    public void invoke_delete_when_handle_program_tracked_entity_attribute_set_as_deleted() throws Exception {
        when(programTrackedEntityAttribute.deleted()).thenReturn(Boolean.TRUE);

        programTrackedEntityAttributeHandler.handleProgramTrackedEntityAttributes(programTrackedEntityAttributes);

        // verify that delete is called once
        verify(programTrackedEntityAttributeStore, times(1)).delete(programTrackedEntityAttribute.uid());

        // verify that update and insert is never called
        verify(programTrackedEntityAttributeStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt(), anyString());

        verify(programTrackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt());

        // verify that tracked entity attribute handler is called once
        verify(trackedEntityAttributeHandler, times(1)).handleTrackedEntityAttribute(
                programTrackedEntityAttribute.trackedEntityAttribute()
        );

    }

    @Test
    public void invoke_only_update_when_handle_program_stage_section_inserted() throws Exception {
        when(programTrackedEntityAttributeStore.update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(),
                anyString(), anyInt(), anyString())
        ).thenReturn(1);

        programTrackedEntityAttributeHandler.handleProgramTrackedEntityAttributes(programTrackedEntityAttributes);

        // verify that update is called once
        verify(programTrackedEntityAttributeStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt(), anyString());

        // verify that insert and delete is never called
        verify(programTrackedEntityAttributeStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(), anyString(), anyInt());

        verify(programTrackedEntityAttributeStore, never()).delete(anyString());

        // verify that tracked entity attribute handler is called once
        verify(trackedEntityAttributeHandler, times(1)).handleTrackedEntityAttribute(
                programTrackedEntityAttribute.trackedEntityAttribute()
        );

    }

    @Test
    public void invoke_update_when_handle_program_tracked_entity_attribute_mark_as_inserted() throws Exception {
        when(programTrackedEntityAttributeStore.update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(),
                anyString(), anyInt(), anyString())
        ).thenReturn(0);

        programTrackedEntityAttributeHandler.handleProgramTrackedEntityAttributes(programTrackedEntityAttributes);

        // verify that insert is called once
        verify(programTrackedEntityAttributeStore, times(1)).insert(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(),
                anyString(), anyInt());

        // verify that update is called once since we try to update before we insert
        verify(programTrackedEntityAttributeStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                anyString(), anyBoolean(), anyBoolean(),
                anyString(), anyInt(), anyString());

        // verify that delete is never called
        verify(programTrackedEntityAttributeStore, never()).delete(anyString());

        // verify that tracked entity attribute handler is called once
        verify(trackedEntityAttributeHandler, times(1)).handleTrackedEntityAttribute(
                programTrackedEntityAttribute.trackedEntityAttribute()
        );

    }
}
