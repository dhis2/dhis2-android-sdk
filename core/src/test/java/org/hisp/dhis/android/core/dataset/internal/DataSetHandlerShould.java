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

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.dataset.DataSetInternalAccessor;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Matchers.any;
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
    private Handler<Section> sectionHandler;

    @Mock
    private OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    @Mock
    private Handler<DataElementOperand> compulsoryDataElementOperandHandler;

    @Mock
    private LinkHandler<DataElementOperand, DataSetCompulsoryDataElementOperandLink>
            dataSetCompulsoryDataElementOperandLinkHandler;

    @Mock
    private LinkHandler<DataInputPeriod, DataInputPeriod> dataInputPeriodHandler;

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
    private LinkHandler<DataSetElement, DataSetElement> dataSetElementLinkHandler;

    @Mock
    private LinkHandler<Indicator, DataSetIndicatorLink> dataSetIndicatorLinkHandler;

    @Mock
    private CollectionCleaner<DataSet> collectionCleaner;

    @Mock
    private LinkCleaner<DataSet> linkCleaner;

    @Mock
    private List<DataInputPeriod> dataInputPeriods;

    // object to test
    private DataSetHandler dataSetHandler;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        dataSetHandler = new DataSetHandler(dataSetStore,
                sectionHandler,
                sectionOrphanCleaner,
                compulsoryDataElementOperandHandler,
                dataSetCompulsoryDataElementOperandLinkHandler,
                dataInputPeriodHandler,
                dataSetElementLinkHandler,
                dataSetIndicatorLinkHandler,
                collectionCleaner,
                linkCleaner);

        when(dataSet.uid()).thenReturn("dataset_uid");
        when(dataSet.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.write()).thenReturn(true);

        dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        sections = new ArrayList<>();
        sections.add(section);
        when(DataSetInternalAccessor.accessSections(dataSet)).thenReturn(sections);

        compulsoryDataElementOperands = new ArrayList<>();
        compulsoryDataElementOperands.add(compulsoryDataElementOperand);
        when(dataSet.compulsoryDataElementOperands()).thenReturn(compulsoryDataElementOperands);

        dataInputPeriods = new ArrayList<>();
        dataInputPeriods.add(dataInputPeriod);
        when(dataSet.dataInputPeriods()).thenReturn(dataInputPeriods);
    }

    @Test
    public void not_perform_any_action_passing_null_arguments() {

        dataSetHandler.handle(null);

        verify(dataSetStore, never()).delete(anyString());
        verify(dataSetStore, never()).update(any(DataSet.class));
        verify(dataSetStore, never()).insert(any(DataSet.class));

        verify(sectionHandler, never()).handleMany(anyList());

        verify(compulsoryDataElementOperandHandler, never()).handleMany(anyList());

        verify(dataInputPeriodHandler, never()).handleMany(anyString(), anyList(), any());
    }

    @Test
    public void handle_nested_sections() {

        dataSetHandler.handle(dataSet);

        verify(sectionHandler).handleMany(anyList());
    }

    @Test
    public void delete_orphan_sections() {

        when(dataSetStore.updateOrInsert(any(DataSet.class))).thenReturn(HandleAction.Update);
        dataSetHandler.handle(dataSet);

        verify(sectionOrphanCleaner).deleteOrphan(dataSet, sections);
    }

    @Test
    public void not_delete_orphan_sections_inserting() {

        when(dataSetStore.updateOrInsert(any(DataSet.class))).thenReturn(HandleAction.Insert);
        dataSetHandler.handle(dataSet);

        verify(sectionOrphanCleaner, never()).deleteOrphan(dataSet, sections);
    }

    @Test
    public void handle_nested_compulsory_data_elements_operands() {

        dataSetHandler.handle(dataSet);

        verify(compulsoryDataElementOperandHandler).handleMany(anyList());
    }

    @Test
    public void handle_data_set_compulsory_data_element_operand_link() {

        dataSetHandler.handle(dataSet);

        verify(dataSetCompulsoryDataElementOperandLinkHandler).handleMany(eq(dataSet.uid()), anyList(), any());
    }

    @Test
    public void handle_nested_data_input_periods() {

        dataSetHandler.handle(dataSet);

        verify(dataInputPeriodHandler).handleMany(anyString(), anyList(), any());
    }

    @Test
    public void handle_data_element_links() {

        dataSetHandler.handle(dataSet);

        verify(dataSetElementLinkHandler).handleMany(anyString(), anyList(), any());
    }

    @Test
    public void handle_indicator_links() {

        dataSetHandler.handle(dataSet);

        verify(dataSetIndicatorLinkHandler).handleMany(anyString(), anyList(), any());
    }
}