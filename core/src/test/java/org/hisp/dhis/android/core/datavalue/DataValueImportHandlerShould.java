package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataValueImportHandlerShould {

    @Mock
    DataValueStore dataValueStore;

    @Mock
    ImportSummary importSummary;

    @Mock
    DataValueSet dataValueSet;

    @Mock
    DataValue dataValue;

    private DataValueImportHandler dataValueImportHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        dataValueImportHandler = new DataValueImportHandler(dataValueStore);
    }

    @Test
    public void passingNullDataValueSet_shouldNotPerformAnyAction() {

        dataValueImportHandler.handleImportSummary(null, importSummary);

        verify(dataValueStore, never()).setState(anyString(), any(State.class));
    }

    @Test
    public void passingNullImportSummary_shouldNotPerformAnyAction() {

        dataValueImportHandler.handleImportSummary(dataValueSet, null);

        verify(dataValueStore, never()).setState(anyString(), any(State.class));
    }

    @Test
    public void successfullyImportedDataValues_shouldBeMarkedAsSynced() {

        Collection<DataValue> dataValueCollection = new ArrayList<>();
        dataValueCollection.add(dataValue);

        dataValueSet.dataValues = dataValueCollection;

        when(dataValue.dataElement()).thenReturn("test_data_value_uid");
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);

        dataValueImportHandler.handleImportSummary(dataValueSet, importSummary);

        verify(dataValueStore, times(1)).setState("test_data_value_uid", State.SYNCED);
    }

    @Test
    public void unsuccessfullyImportedDataValues_shouldBeMarkedAsError() {

        Collection<DataValue> dataValueCollection = new ArrayList<>();
        dataValueCollection.add(dataValue);

        dataValueSet.dataValues = dataValueCollection;

        when(dataValue.dataElement()).thenReturn("test_data_value_uid");
        when(importSummary.importStatus()).thenReturn(ImportStatus.ERROR);

        dataValueImportHandler.handleImportSummary(dataValueSet, importSummary);

        verify(dataValueStore, times(1)).setState("test_data_value_uid", State.ERROR);
    }

}
