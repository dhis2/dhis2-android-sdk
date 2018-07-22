/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataElementOperandHandlerShould {

    @Mock
    private IdentifiableObjectStore<DataElementOperandModel> dataElementOperandStore;

    @Mock
    private DataElementOperand dataElementOperand;

    // object to test
    private DataElementOperandHandler dataElementOperandHandler;


    @Before
    public void setUp() throws Exception {
        
        MockitoAnnotations.initMocks(this);

        dataElementOperandHandler = new DataElementOperandHandler(dataElementOperandStore);

        when(dataElementOperand.uid()).thenReturn("dataElementOprand_uid");
        when(dataElementOperand.dataElementUid()).thenReturn("dataElement_uid");
        when(dataElementOperand.categoryOptionComboUid()).thenReturn("categoryOptionCombo_uid");
    }

    @Test
    public void passingNullArguments_shouldNotPerformAnyAction() {

       dataElementOperandHandler.handle(null, null);

        verify(dataElementOperandStore, never()).delete(anyString());
        verify(dataElementOperandStore, never()).update(any(DataElementOperandModel.class));
        verify(dataElementOperandStore, never()).insert(any(DataElementOperandModel.class));
    }

}
