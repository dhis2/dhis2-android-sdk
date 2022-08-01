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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;
import org.hisp.dhis.android.core.imports.internal.ImportCount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataSetCompleteRegistrationImportHandlerShould {

    @Mock
    DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;

    @Mock
    DataValueImportSummary dataValueImportSummary;

    @Mock
    DataSetCompleteRegistrationPayload dataSetCompleteRegistrationPayload;

    @Mock
    DataSetCompleteRegistration dataSetCompleteRegistration;

    private DataSetCompleteRegistrationImportHandler dataSetCompleteRegistrationImportHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        dataSetCompleteRegistrationImportHandler
                = new DataSetCompleteRegistrationImportHandler(dataSetCompleteRegistrationStore);
        when(dataValueImportSummary.importCount()).thenReturn(ImportCount.EMPTY);
        when(dataValueImportSummary.responseType()).thenReturn("ImportSummary");
    }

    @Test
    public void not_perform_any_action_passing_empty_data_value_set() {

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        dataSetCompleteRegistrationImportHandler.handleImportSummary(
                new DataSetCompleteRegistrationPayload(new ArrayList<>()),
                dataValueImportSummary, new ArrayList<>(), new ArrayList<>());

        verify(dataSetCompleteRegistrationStore, never()).setState(
                any(DataSetCompleteRegistration.class), any(State.class));
    }

    @Test
    public void mark_as_synced_when_successfully_imported_data_values() {

        List<DataSetCompleteRegistration> dataSetCompleteRegistrations = new ArrayList<>();
        dataSetCompleteRegistrations.add(dataSetCompleteRegistration);

        dataSetCompleteRegistrationPayload.dataSetCompleteRegistrations = dataSetCompleteRegistrations;

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(dataSetCompleteRegistrationStore.isBeingUpload(any(DataSetCompleteRegistration.class)))
                .thenReturn(Boolean.TRUE);

        dataSetCompleteRegistrationImportHandler.handleImportSummary(dataSetCompleteRegistrationPayload,
                dataValueImportSummary, new ArrayList<>(), new ArrayList<>());

        verify(dataSetCompleteRegistrationStore, times(1)).setState(dataSetCompleteRegistration, State.SYNCED);
    }

    @Test
    public void mark_as_error_when_unsuccessfully_imported_data_values() {

        List<DataSetCompleteRegistration> dataValueCollection = new ArrayList<>();
        dataValueCollection.add(dataSetCompleteRegistration);

        dataSetCompleteRegistrationPayload.dataSetCompleteRegistrations = dataValueCollection;

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.ERROR);
        when(dataSetCompleteRegistrationStore.isBeingUpload(any(DataSetCompleteRegistration.class)))
                .thenReturn(Boolean.TRUE);

        dataSetCompleteRegistrationImportHandler.handleImportSummary(
                dataSetCompleteRegistrationPayload, dataValueImportSummary, new ArrayList<>(), new ArrayList<>());

        verify(dataSetCompleteRegistrationStore, times(1)).setState(dataSetCompleteRegistration, State.ERROR);
    }
}