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

import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModelBuilder;
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
    private IdentifiableObjectStore<DataSet> dataSetStore;

    @Mock
    private SyncHandlerWithTransformer<ObjectStyle> styleHandler;

    @Mock
    private SyncHandler<Section> sectionHandler;

    @Mock
    private OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    @Mock
    private SyncHandler<DataElementOperand> compulsoryDataElementOperandHandler;

    @Mock
    private LinkModelHandler<DataElementOperand,
            DataSetCompulsoryDataElementOperandLinkModel> dataSetCompulsoryDataElementOperandLinkHandler;

    @Mock
    private LinkSyncHandlerWithTransformer<DataInputPeriod> dataInputPeriodHandler;

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

    @Mock
    private DataInputPeriod dataInputPeriod;

    @Mock
    private LinkSyncHandler<DataSetElement> dataSetElementLinkHandler;

    @Mock
    private LinkModelHandler<ObjectWithUid, DataSetIndicatorLinkModel> dataSetIndicatorLinkHandler;

    @Mock
    private CollectionCleaner<DataSet> collectionCleaner;

    @Mock
    private List<DataInputPeriod> dataInputPeriods;

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
                dataSetCompulsoryDataElementOperandLinkHandler,
                dataInputPeriodHandler,
                dataSetElementLinkHandler,
                dataSetIndicatorLinkHandler,
                collectionCleaner);

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

        dataInputPeriods = new ArrayList<>();
        dataInputPeriods.add(dataInputPeriod);
        when(dataSet.dataInputPeriods()).thenReturn(dataInputPeriods);
    }

    @Test
    public void not_perform_any_action_passing_null_arguments() {

        dataSetHandler.handle(null, null);

        verify(dataSetStore, never()).delete(anyString());
        verify(dataSetStore, never()).update(any(DataSet.class));
        verify(dataSetStore, never()).insert(any(DataSet.class));

        verify(sectionHandler, never()).handleMany(anyListOf(Section.class));

        verify(compulsoryDataElementOperandHandler, never()).handleMany(anyListOf(DataElementOperand.class));

        verify(dataInputPeriodHandler, never()).handleMany(anyString(),
                anyListOf(DataInputPeriod.class),
                Matchers.<ModelBuilder<DataInputPeriod, DataInputPeriod>>any());
    }

    @Test
    public void handle_nested_sections() {

        dataSetHandler.handle(dataSet);

        verify(sectionHandler).handleMany(anyListOf(Section.class));
    }

    @Test
    public void delete_orphan_sections() {

        when(dataSetStore.updateOrInsert(any(DataSet.class))).thenReturn(HandleAction.Update);
        dataSetHandler.handle(dataSet);

        verify(sectionOrphanCleaner).deleteOrphan(dataSet, dataSet.sections());
    }

    @Test
    public void not_delete_orphan_sections_inserting() {

        when(dataSetStore.updateOrInsert(any(DataSet.class))).thenReturn(HandleAction.Insert);
        dataSetHandler.handle(dataSet);

        verify(sectionOrphanCleaner, never()).deleteOrphan(dataSet, dataSet.sections());
    }

    @Test
    public void handle_nested_compulsory_data_elements_operands() {

        dataSetHandler.handle(dataSet);

        verify(compulsoryDataElementOperandHandler).handleMany(anyListOf(DataElementOperand.class));
    }

    @Test
    public void handle_data_set_compulsory_data_element_operand_link() {

        dataSetHandler.handle(dataSet);

        verify(dataSetCompulsoryDataElementOperandLinkHandler).handleMany(eq(dataSet.uid()), eq(compulsoryDataElementOperands),
                any(DataSetCompulsoryDataElementOperandLinkModelBuilder.class));
    }

    @Test
    public void handle_nested_data_input_periods() {

        dataSetHandler.handle(dataSet);

        verify(dataInputPeriodHandler).handleMany(anyString(),
                anyListOf(DataInputPeriod.class),
                Matchers.<ModelBuilder<DataInputPeriod, DataInputPeriod>>any());
    }

    @Test
    public void handle_style() {

        dataSetHandler.handle(dataSet);

        verify(styleHandler).handle(eq(dataSet.style()), any(ObjectStyleModelBuilder.class));
    }

    @Test
    public void handle_data_element_links() {

        dataSetHandler.handle(dataSet);

        verify(dataSetElementLinkHandler).handleMany(anyString(), anyListOf(DataSetElement.class));
    }

    @Test
    public void handle_indicator_links() {

        dataSetHandler.handle(dataSet);

        verify(dataSetIndicatorLinkHandler).handleMany(anyString(), anyListOf(ObjectWithUid.class),
                any(DataSetIndicatorLinkModelBuilder.class));
    }
}