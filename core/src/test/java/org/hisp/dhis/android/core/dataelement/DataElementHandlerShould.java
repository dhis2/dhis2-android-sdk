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
package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.common.DictionaryTableHandler;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithUid;
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
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataElementHandlerShould {

    @Mock
    private IdentifiableObjectStore<DataElement> dataElementStore;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    @Mock
    private DataElement dataElement;

    @Mock
    private ObjectWithUid categoryCombo;

    // object to test
    private DataElementHandler dataElementHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataElementHandler = new DataElementHandler(dataElementStore, styleHandler, renderTypeHandler);
        when(dataElement.uid()).thenReturn("test_data_element_uid");
        when(dataElement.categoryCombo()).thenReturn(categoryCombo);
    }

    @Test
    public void call_style_handler() throws Exception {
        dataElementHandler.handle(dataElement);
        verify(styleHandler).handle(eq(dataElement.style()), any(ObjectStyleModelBuilder.class));
    }

    @Test
    public void call_render_type_handler() throws Exception {
        dataElementHandler.handle(dataElement);
        verify(renderTypeHandler).handle(dataElement.renderType(), dataElement.uid(), DataElementModel.TABLE);
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableSyncHandlerImpl<DataElement> genericHandler =
                new DataElementHandler(null, null, renderTypeHandler);
    }
}
