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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class OptionHandlerShould {
    @Mock
    private OptionStore optionStore;

    @Mock
    private Option option;

    @Mock
    private OptionSet optionSet;

    // object to test
    private OptionHandler optionHandler;

    // list of options
    private List<Option> options;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        optionHandler = new OptionHandler(optionStore);

        when(optionSet.uid()).thenReturn("test_option_set_uid");
        when(option.uid()).thenReturn("test_option_uid");
        when(option.optionSet()).thenReturn(optionSet);

        options = new ArrayList<>();
        options.add(option);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        optionHandler.handleOptions(null);
        verify(optionStore, never()).delete(anyString());
        verify(optionStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString());
        verify(optionStore, never()).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString());
    }

    @Test
    public void invoke_only_delete_when_option_is_set_as_deleted() throws Exception {
        when(option.deleted()).thenReturn(Boolean.TRUE);

        optionHandler.handleOptions(options);

        // verify that option is deleted
        verify(optionStore, times(1)).delete(option.uid());

        // verify that update and insert is never called
        verify(optionStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString());
        verify(optionStore, never()).update(
                anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString());
    }

    @Test
    public void invoke_only_update_when_handle_options_inserted() throws Exception {
        when(optionStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString())).thenReturn(1);

        optionHandler.handleOptions(options);

        // verify that update is called once
        verify(optionStore, times(1)).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString());

        // verify that insert and delete is never called
        verify(optionStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString());
        verify(optionStore, never()).delete(anyString());

    }

    @Test
    public void invoke_update_and_insert_when_handle_options_not_inserted() throws Exception {
        when(optionStore.update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString())).thenReturn(0);

        optionHandler.handleOptions(options);

        // verify that insert is called once
        verify(optionStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString());

        // verify that update is called once since we try to update before we insert
        verify(optionStore, times(1)).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString());

        // verify that delete is never called
        verify(optionStore, never()).delete(anyString());

    }
}
