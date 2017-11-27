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
package org.hisp.dhis.android.core.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ResourceHandlerShould {

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private Date serverDate;

    // object to test
    private ResourceHandler resourceHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        resourceHandler = new ResourceHandler(resourceStore);
    }

    @Test
    public void do_nothing_when_passing_null_resource() throws Exception {
        resourceHandler.handleResource(null, serverDate);

        // verify that store is never called
        verify(resourceStore, never()).insert(anyString(), any(Date.class));
        verify(resourceStore, never()).update(anyString(), any(Date.class), anyString());
    }

    @Test
    public void do_nothing_when_passing_null_server_date() throws Exception {
        resourceHandler.handleResource(ResourceModel.Type.PROGRAM, null);

        // verify that store is never called
        verify(resourceStore, never()).insert(anyString(), any(Date.class));
        verify(resourceStore, never()).update(anyString(), any(Date.class), anyString());
    }

    @Test
    public void invoke_only_update_when_handle_resource_updatable() throws Exception {
        when(resourceStore.update(anyString(), any(Date.class), anyString())).thenReturn(1);

        resourceHandler.handleResource(ResourceModel.Type.PROGRAM, serverDate);

        verify(resourceStore, times(1)).update(anyString(), any(Date.class), anyString());

        // verify that insert is never called
        verify(resourceStore, never()).insert(anyString(), any(Date.class));
    }

    @Test
    public void invoke_update_and_insert_when_handle_resource_not_inserted() throws Exception {
        when(resourceStore.update(anyString(), any(Date.class), anyString())).thenReturn(0);

        resourceHandler.handleResource(ResourceModel.Type.PROGRAM, serverDate);

        // verify that insert is called once
        verify(resourceStore, times(1)).insert(anyString(), any(Date.class));

        // verify that update is called since we try to update before we insert
        verify(resourceStore, times(1)).update(anyString(), any(Date.class), anyString());
    }
}
