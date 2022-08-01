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

package org.hisp.dhis.android.core.datavalue.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueConflict;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataValueImportHandlerShould {

    @Mock
    DataValueStore dataValueStore;

    @Mock
    DataValueConflictParser dataValueConflictParser;

    @Mock
    ObjectStore<DataValueConflict> dataValueConflictStore;

    @Mock
    DataValueImportSummary dataValueImportSummary;

    DataValueSet dataValueSet;

    @Mock
    DataValue dataValue;

    private DataValueImportHandler dataValueImportHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        dataValueSet = new DataValueSet(Collections.singletonList(dataValue));

        dataValueImportHandler = new DataValueImportHandler(dataValueStore, dataValueConflictParser, dataValueConflictStore);
    }

    @Test
    public void passingNullDataValueSet_shouldNotPerformAnyAction() {

        dataValueImportHandler.handleImportSummary(null, dataValueImportSummary);

        verify(dataValueStore, never()).setState(any(DataValue.class), any(State.class));
    }

    @Test
    public void passingNullImportSummary_shouldNotPerformAnyAction() {

        dataValueImportHandler.handleImportSummary(dataValueSet, null);

        verify(dataValueStore, never()).setState(any(DataValue.class), any(State.class));
    }

    @Test
    public void successfullyImportedDataValues_shouldBeMarkedAsSynced() {

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(dataValueStore.isDataValueBeingUpload(any(DataValue.class))).thenReturn(Boolean.TRUE);

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary);

        verify(dataValueStore, times(1)).setState(dataValue, State.SYNCED);
    }

    @Test
    public void successfullyImportedAndDeletedDataValues_shouldBeDeleted() {

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(dataValueStore.isDataValueBeingUpload(any(DataValue.class))).thenReturn(Boolean.TRUE);
        when(dataValueStore.isDeleted(any(DataValue.class))).thenReturn(Boolean.TRUE);

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary);

        verify(dataValueStore, never()).setState(any(DataValue.class), any());
        verify(dataValueStore, times(1)).deleteWhere(dataValue);
    }

    @Test
    public void unsuccessfullyImportedDataValues_shouldBeMarkedAsError() {

        when(dataValueImportSummary.importStatus()).thenReturn(ImportStatus.ERROR);
        when(dataValueStore.isDataValueBeingUpload(any(DataValue.class))).thenReturn(Boolean.TRUE);

        dataValueImportHandler.handleImportSummary(dataValueSet, dataValueImportSummary);

        verify(dataValueStore, times(1)).setState(dataValue, State.ERROR);
    }
}
