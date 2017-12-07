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

import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OptionSetHandlerShould {
    @Mock
    private OptionSetStore optionSetStore;

    @Mock
    private OptionSet optionSet;

    @Mock
    private OptionHandler optionHandler;

    // object to test
    private OptionSetHandler optionSetHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);
        when(optionSet.uid()).thenReturn("test_option_set_uid");
    }

    @Test
    public void do_nothing_when_passing_in_null() throws Exception {
        optionSetHandler.handleOptionSet(null);

        // verify that delete, update and insert is never called
        verify(optionSetStore, never()).delete(anyString());

        verify(optionSetStore, never()).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString());

        verify(optionSetStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class));

    }

    @Test
    public void invoke_delete_when_handle_option_set_set_as_deleted() throws Exception {
        when(optionSet.deleted()).thenReturn(Boolean.TRUE);

        optionSetHandler.handleOptionSet(optionSet);

        // verify that delete is called once
        verify(optionSetStore, times(1)).delete(optionSet.uid());

        // verify that update and insert is never called
        verify(optionSetStore, never()).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString());

        verify(optionSetStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class));

    }

    @Test
    public void invoke_only_update_when_handle_option_set_inserted() throws Exception {
        when(optionSetStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString())).thenReturn(1);

        optionSetHandler.handleOptionSet(optionSet);

        // verify that update is called once
        verify(optionSetStore, times(1)).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString());

        // verify that insert and delete is never called
        verify(optionSetStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class));

        verify(optionSetStore, never()).delete(anyString());
    }

    @Test
    public void invoke_update_and_insert_when_handle_option_set_not_inserted() throws Exception {
        when(optionSetStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString())).thenReturn(0);

        optionSetHandler.handleOptionSet(optionSet);

        // verify that insert is called once
        verify(optionSetStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class));

        // verify that update is called once since we update before we insert
        verify(optionSetStore, times(1)).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyInt(), any(ValueType.class), anyString());

        // verify that delete is never called
        verify(optionSetStore, never()).delete(anyString());
    }
}
