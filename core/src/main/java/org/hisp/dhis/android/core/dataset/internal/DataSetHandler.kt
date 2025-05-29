/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandHandler
import org.hisp.dhis.android.core.dataset.*
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorLinkHandler
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class DataSetHandler(
    dataSetStore: DataSetStore,
    private val sectionHandler: SectionHandler,
    private val sectionOrphanCleaner: SectionOrphanCleaner,
    private val compulsoryDataElementOperandHandler: DataElementOperandHandler,
    private val dataSetCompulsoryDataElementOperandLinkHandler: DataSetCompulsoryDataElementOperandHandler,
    private val dataInputPeriodHandler: DataInputPeriodHandler,
    private val dataSetElementLinkHandler: DataSetElementHandler,
    private val dataSetIndicatorLinkHandler: DataSetIndicatorLinkHandler,
    private val collectionCleaner: DataSetCollectionCleaner,
    private val linkCleaner: DataSetOrganisationUnitLinkCleaner,
) : IdentifiableHandlerImpl<DataSet>(dataSetStore) {

    override suspend fun afterObjectHandled(o: DataSet, action: HandleAction) {
        val sections = DataSetInternalAccessor.accessSections(o)
        sectionHandler.handleMany(sections)
        compulsoryDataElementOperandHandler.handleMany(o.compulsoryDataElementOperands())
        dataSetCompulsoryDataElementOperandLinkHandler.handleMany(
            o.uid(),
            o.compulsoryDataElementOperands(),
        ) { dataElementOperand: DataElementOperand ->
            DataSetCompulsoryDataElementOperandLink.builder()
                .dataSet(o.uid())
                .dataElementOperand(dataElementOperand.uid())
                .build()
        }
        dataInputPeriodHandler.handleMany(
            o.uid(),
            o.dataInputPeriods(),
        ) { dataInputPeriod: DataInputPeriod ->
            dataInputPeriod.toBuilder().dataSet(ObjectWithUid.create(o.uid())).build()
        }
        dataSetElementLinkHandler.handleMany(
            o.uid(),
            o.dataSetElements(),
        ) { dataSetElement: DataSetElement ->
            dataSetElement.toBuilder().dataSet(ObjectWithUid.create(o.uid())).build()
        }
        dataSetIndicatorLinkHandler.handleMany(
            o.uid(),
            o.indicators(),
        ) { indicator: Indicator ->
            DataSetIndicatorLink.builder().dataSet(o.uid()).indicator(indicator.uid()).build()
        }

        if (action === HandleAction.Update) {
            sectionOrphanCleaner.deleteOrphan(o, sections)
        }
    }

    override suspend fun afterCollectionHandled(oCollection: Collection<DataSet>?) {
        collectionCleaner.deleteNotPresent(oCollection)
        linkCleaner.deleteNotPresent(oCollection)
    }
}
