/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.common.valuetype.rendering.internal;

import org.hisp.dhis.android.core.arch.handlers.internal.DictionaryTableHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ValueTypeRenderingHandlerShould {

    private static final String UID = "uid";
    private static final String TABLE = "table";
    
    @Mock
    private HandlerWithTransformer<ValueTypeDeviceRendering> deviceRenderingHandler;
    
    @Mock
    private ValueTypeDeviceRendering desktopRenderType;
    
    @Mock
    private ValueTypeDeviceRendering mobileRenderType;
    
    @Mock
    private ValueTypeRendering renderType;

    // object to test
    private DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(renderType.desktop()).thenReturn(desktopRenderType);
        when(renderType.mobile()).thenReturn(mobileRenderType);
        renderTypeHandler = new ValueTypeRenderingHandler(deviceRenderingHandler);
    }

    @Test
    public void call_device_handler_when_render_type_not_null() throws Exception {
        renderTypeHandler.handle(renderType, UID, TABLE);
        verify(deviceRenderingHandler).handle(eq(desktopRenderType), any());
        verify(deviceRenderingHandler).handle(eq(mobileRenderType), any());
        verifyNoMoreInteractions(deviceRenderingHandler);
    }

    @Test
    public void not_call_device_handler_when_render_type_null() throws Exception {
        renderTypeHandler.handle(null, UID, TABLE);
        verifyNoMoreInteractions(deviceRenderingHandler);
    }
}
