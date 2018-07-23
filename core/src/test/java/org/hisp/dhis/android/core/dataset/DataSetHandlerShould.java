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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandModel;
import org.hisp.dhis.android.core.dataelement.DataElementOperandModelBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataSetHandlerShould {

    @Mock
    private IdentifiableObjectStore<DataSetModel> dataSetStore;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private GenericHandler<Section, SectionModel> sectionHandler;

    @Mock
    private OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    @Mock
    private GenericHandler<DataElementOperand, DataElementOperandModel> compulsoryDataElementOperandHandler;

    @Mock
    private LinkModelHandler<DataElementOperand,
            DataSetCompulsoryDataElementOperandLinkModel> dataSetCompulsoryDataElementOperandLinkHandler;

    @Mock
    private DataSet dataSet;

    @Mock
    private List<DataSet> dataSets;

    @Mock
    private Access access;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Section section;

    @Mock
    private List<Section> sections;

    @Mock
    private DataElementOperand compulsoryDataElementOperand;

    @Mock
    private List<DataElementOperand> compulsoryDataElementOperands;

    // object to test
    private DataSetHandler dataSetHandler;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        dataSetHandler = new DataSetHandler(dataSetStore,
                styleHandler,
                sectionHandler,
                sectionOrphanCleaner,
                compulsoryDataElementOperandHandler,
                dataSetCompulsoryDataElementOperandLinkHandler);

        when(dataSet.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.write()).thenReturn(true);

        dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        sections = new ArrayList<>();
        sections.add(section);
        when(dataSet.sections()).thenReturn(sections);

        compulsoryDataElementOperands = new ArrayList<>();
        compulsoryDataElementOperands.add(compulsoryDataElementOperand);
        when(dataSet.compulsoryDataElementOperands()).thenReturn(compulsoryDataElementOperands);
    }

    @Test
    public void passingNullArguments_shouldNotPerformAnyAction() {

        dataSetHandler.handle(null, null);

        verify(dataSetStore, never()).delete(anyString());
        verify(dataSetStore, never()).update(any(DataSetModel.class));
        verify(dataSetStore, never()).insert(any(DataSetModel.class));

        verify(sectionHandler, never()).handleMany(anyListOf(Section.class),
                Matchers.<ModelBuilder<Section, SectionModel>>any());

        verify(compulsoryDataElementOperandHandler, never()).handleMany(anyListOf(DataElementOperand.class),
                Matchers.<ModelBuilder<DataElementOperand, DataElementOperandModel>>any());
    }

    @Test
    public void handlingDataSet_shouldHandleNestedSections() {

        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(sectionHandler).handleMany(anyListOf(Section.class),
                any(SectionModelBuilder.class));
    }

    @Test
    public void updating_shouldDeleteOrhpanSections() {

        when(dataSetStore.updateOrInsert(any(DataSetModel.class))).thenReturn(HandleAction.Update);
        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(sectionOrphanCleaner).deleteOrphan(dataSet, dataSet.sections());
    }

    @Test
    public void inserting_shouldNotDeleteOrphanSections() {

        when(dataSetStore.updateOrInsert(any(DataSetModel.class))).thenReturn(HandleAction.Insert);
        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(sectionOrphanCleaner, never()).deleteOrphan(dataSet, dataSet.sections());
    }

    @Test
    public void handlingDataSet_shouldHandleNestedCompulsoryDataElementOperands() {

        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(compulsoryDataElementOperandHandler).handleMany(anyListOf(DataElementOperand.class),
                any(DataElementOperandModelBuilder.class));
    }

    @Test
    public void handlingDataSet_shouldHandleDataSetCompulsoryDataElementOprandLink() {

        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(dataSetCompulsoryDataElementOperandLinkHandler).handleMany(eq(dataSet.uid()), eq(compulsoryDataElementOperands),
                any(DataSetCompulsoryDataElementOperandLinkModelBuilder.class));
    }

    @Test
    public void handlingDataSet_shouldHandleStyle() {

        dataSetHandler.handle(dataSet, new DataSetModelBuilder());

        verify(styleHandler).handle(eq(dataSet.style()), any(ObjectStyleModelBuilder.class));
    }
}