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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.option.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataElementHandlerShould {

    @Mock
    private IdentifiableObjectStore<DataElementModel> dataSetStore;

    @Mock
    private GenericHandler<OptionSet> optionSetHandler;

    @Mock
    private ObjectStyleHandler styleHandler;

    @Mock
    private DataElement dataElement;

    @Mock
    private ObjectWithUid categoryCombo;

    @Mock
    private OptionSet optionSet;

    // object to test
    private DataElementHandler dataElementHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataElementHandler = new DataElementHandler(dataSetStore, optionSetHandler, styleHandler);
        when(dataElement.uid()).thenReturn("test_data_element_uid");
        when(dataElement.optionSet()).thenReturn(optionSet);
        when(dataElement.categoryCombo()).thenReturn(categoryCombo);
    }

    @Test
    public void call_option_set_handler() throws Exception {
        dataElementHandler.handle(dataElement);
        verify(optionSetHandler).handle(optionSet);
    }

    @Test
    public void call_style_handler() throws Exception {
        dataElementHandler.handle(dataElement);
        verify(styleHandler).handle(dataElement.style(), dataElement.uid(), DataElementModel.TABLE);
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<DataElement, DataElementModel> genericHandler = new DataElementHandler(
                null,null, null);
    }
}
