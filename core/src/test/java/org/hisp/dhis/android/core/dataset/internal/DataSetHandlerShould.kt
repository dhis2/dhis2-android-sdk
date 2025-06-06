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
package org.hisp.dhis.android.core.dataset.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandHandler
import org.hisp.dhis.android.core.dataset.DataInputPeriod
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetInternalAccessor
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorLinkHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataSetHandlerShould {
    private val dataSetStore: DataSetStore = mock()
    private val sectionHandler: SectionHandler = mock()
    private val sectionOrphanCleaner: SectionOrphanCleaner = mock()
    private val compulsoryDataElementOperandHandler: DataElementOperandHandler = mock()
    private val dataSetCompulsoryDataElementOperandLinkHandler: DataSetCompulsoryDataElementOperandHandler = mock()
    private val dataInputPeriodHandler: DataInputPeriodHandler = mock()
    private val dataSet: DataSet = mock()
    private var dataSets: MutableList<DataSet> = mock()
    private val access: Access = mock()
    private val dataAccess: DataAccess = mock()
    private val section: Section = mock()
    private var sections: MutableList<Section> = mock()
    private val compulsoryDataElementOperand: DataElementOperand = mock()
    private var compulsoryDataElementOperands: MutableList<DataElementOperand> = mock()
    private val dataInputPeriod: DataInputPeriod = mock()
    private val dataSetElementHandler: DataSetElementHandler = mock()
    private val dataSetIndicatorLinkHandler: DataSetIndicatorLinkHandler = mock()
    private val collectionCleaner: DataSetCollectionCleaner = mock()
    private val linkCleaner: DataSetOrganisationUnitLinkCleaner = mock()
    private var dataInputPeriods: MutableList<DataInputPeriod> = mock()

    // object to test
    private lateinit var dataSetHandler: DataSetHandler

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        dataSetHandler = DataSetHandler(
            dataSetStore,
            sectionHandler,
            sectionOrphanCleaner,
            compulsoryDataElementOperandHandler,
            dataSetCompulsoryDataElementOperandLinkHandler,
            dataInputPeriodHandler,
            dataSetElementHandler,
            dataSetIndicatorLinkHandler,
            collectionCleaner,
            linkCleaner,
        )

        whenever(dataSet.uid()).thenReturn("dataset_uid")
        whenever(dataSet.access()).thenReturn(access)
        whenever(access.data()).thenReturn(dataAccess)
        whenever<Boolean>(dataAccess.write()).thenReturn(true)

        dataSets = mutableListOf(dataSet)
        sections = mutableListOf(section)
        whenever(DataSetInternalAccessor.accessSections(dataSet)).thenReturn(sections)

        compulsoryDataElementOperands = mutableListOf(compulsoryDataElementOperand)
        whenever(dataSet.compulsoryDataElementOperands()).thenReturn(compulsoryDataElementOperands)

        dataInputPeriods = mutableListOf(dataInputPeriod)
        whenever(dataSet.dataInputPeriods()).thenReturn(dataInputPeriods)
        whenever(dataSetStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun not_perform_any_action_passing_null_arguments() = runTest {
        dataSetHandler.handle(null)

        verify(dataSetStore, never()).delete(any())
        verify(dataSetStore, never()).update(any())
        verify(dataSetStore, never()).insert(any<DataSet>())
        verify(sectionHandler, never()).handleMany(ArgumentMatchers.anyList())
        verify(compulsoryDataElementOperandHandler, never()).handleMany(any())
        verify(dataInputPeriodHandler, never()).handleMany(any(), any(), any())
    }

    @Test
    fun handle_nested_sections() = runTest {
        dataSetHandler.handle(dataSet)

        verify(sectionHandler).handleMany(ArgumentMatchers.anyList())
    }

    @Test
    fun delete_orphan_sections() = runTest {
        whenever(dataSetStore.updateOrInsert(any())).thenReturn(HandleAction.Update)
        dataSetHandler.handle(dataSet)

        verify(sectionOrphanCleaner).deleteOrphan(dataSet, sections)
    }

    @Test
    fun not_delete_orphan_sections_inserting() = runTest {
        whenever(dataSetStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
        dataSetHandler.handle(dataSet)

        verify(sectionOrphanCleaner, never()).deleteOrphan(dataSet, sections)
    }

    @Test
    fun handle_nested_compulsory_data_elements_operands() = runTest {
        dataSetHandler.handle(dataSet)

        verify(compulsoryDataElementOperandHandler).handleMany(any())
    }

    @Test
    fun handle_data_set_compulsory_data_element_operand_link() = runTest {
        dataSetHandler.handle(dataSet)

        verify(dataSetCompulsoryDataElementOperandLinkHandler).handleMany(eq(dataSet.uid()), any(), any())
    }

    @Test
    fun handle_nested_data_input_periods() = runTest {
        dataSetHandler.handle(dataSet)

        verify(dataInputPeriodHandler).handleMany(any(), any(), any())
    }

    @Test
    fun handle_data_element_links() = runTest {
        dataSetHandler.handle(dataSet)

        verify(dataSetElementHandler).handleMany(any(), any(), any())
    }

    @Test
    fun handle_indicator_links() = runTest {
        dataSetHandler.handle(dataSet)

        verify(dataSetIndicatorLinkHandler).handleMany(any(), any(), any())
    }
}
