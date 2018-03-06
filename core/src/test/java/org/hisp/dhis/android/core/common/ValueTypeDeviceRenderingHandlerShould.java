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
package org.hisp.dhis.android.core.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.TABLE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.VALUE_TYPE_RENDERING_TYPE;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.VALUE_TYPE_DEVICE_RENDERING_MODEL;

@RunWith(JUnit4.class)
public class ValueTypeDeviceRenderingHandlerShould {

    @Mock
    private ObjectWithoutUidStore<ValueTypeDeviceRenderingModel> store;

    // object to test
    private DictionaryTableHandler<ValueTypeDeviceRendering> deviceRenderTypeHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        deviceRenderTypeHandler = new ValueTypeDeviceRenderingHandler(store, VALUE_TYPE_RENDERING_TYPE.toString());
    }

    @Test
    public void call_store_when_render_type_not_null() throws Exception {
        deviceRenderTypeHandler.handle(VALUE_TYPE_DEVICE_RENDERING_MODEL, UID, TABLE);
        verify(store).updateOrInsertWhere(ValueTypeDeviceRenderingModel.fromPojo(VALUE_TYPE_DEVICE_RENDERING_MODEL,
                UID, TABLE, VALUE_TYPE_RENDERING_TYPE.toString()));
        verifyNoMoreInteractions(store);
    }

    @Test
    public void not_call_store_when_render_type_null() throws Exception {
        deviceRenderTypeHandler.handle(null, UID, TABLE);
        verifyNoMoreInteractions(store);
    }
}
