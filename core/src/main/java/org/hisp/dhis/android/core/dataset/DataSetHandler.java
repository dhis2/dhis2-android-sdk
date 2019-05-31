/*
 * Copyright (c) 2004-2019, University of Oslo
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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.SyncHandler;
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.core.indicator.Indicator;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class DataSetHandler extends IdentifiableSyncHandlerImpl<DataSet> {

    private final ObjectStyleHandler styleHandler;

    private final SyncHandler<Section> sectionHandler;
    private final OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    private final SyncHandler<DataElementOperand> compulsoryDataElementOperandHandler;
    private final LinkSyncHandler<DataElementOperand, DataSetCompulsoryDataElementOperandLink>
            dataSetCompulsoryDataElementOperandLinkHandler;

    private final LinkSyncHandler<DataInputPeriod, DataInputPeriod> dataInputPeriodHandler;
    private final LinkSyncHandler<DataSetElement, DataSetElement> dataSetElementLinkHandler;
    private final LinkSyncHandler<Indicator, DataSetIndicatorLink> dataSetIndicatorLinkHandler;
    private final CollectionCleaner<DataSet> collectionCleaner;

    @Inject
    DataSetHandler(IdentifiableObjectStore<DataSet> dataSetStore,
                   ObjectStyleHandler styleHandler,
                   SyncHandler<Section> sectionHandler,
                   OrphanCleaner<DataSet, Section> sectionOrphanCleaner,
                   SyncHandler<DataElementOperand> compulsoryDataElementOperandHandler,
                   LinkSyncHandler<DataElementOperand, DataSetCompulsoryDataElementOperandLink>
                           dataSetCompulsoryDataElementOperandLinkHandler,
                   LinkSyncHandler<DataInputPeriod, DataInputPeriod> dataInputPeriodHandler,
                   LinkSyncHandler<DataSetElement, DataSetElement> dataSetElementLinkHandler,
                   LinkSyncHandler<Indicator, DataSetIndicatorLink> dataSetIndicatorLinkHandler,
                   CollectionCleaner<DataSet> collectionCleaner) {

        super(dataSetStore);
        this.styleHandler = styleHandler;
        this.sectionHandler = sectionHandler;
        this.sectionOrphanCleaner = sectionOrphanCleaner;
        this.compulsoryDataElementOperandHandler = compulsoryDataElementOperandHandler;
        this.dataSetCompulsoryDataElementOperandLinkHandler = dataSetCompulsoryDataElementOperandLinkHandler;
        this.dataInputPeriodHandler = dataInputPeriodHandler;
        this.dataSetElementLinkHandler = dataSetElementLinkHandler;
        this.dataSetIndicatorLinkHandler = dataSetIndicatorLinkHandler;
        this.collectionCleaner = collectionCleaner;
    }

    @Override
    protected void afterObjectHandled(final DataSet dataSet, HandleAction action) {

        styleHandler.handle(dataSet.style(), dataSet.uid(), DataSetTableInfo.TABLE_INFO.name());

        sectionHandler.handleMany(dataSet.sections());

        compulsoryDataElementOperandHandler.handleMany(dataSet.compulsoryDataElementOperands());

        dataSetCompulsoryDataElementOperandLinkHandler.handleMany(dataSet.uid(),
                dataSet.compulsoryDataElementOperands(),
                dataElementOperand -> DataSetCompulsoryDataElementOperandLink.builder()
                        .dataSet(dataSet.uid()).dataElementOperand(dataElementOperand.uid()).build());

        dataInputPeriodHandler.handleMany(dataSet.uid(),
                dataSet.dataInputPeriods(),
                dataInputPeriod -> dataInputPeriod.toBuilder().dataSet(ObjectWithUid.create(dataSet.uid())).build());

        dataSetElementLinkHandler.handleMany(dataSet.uid(), dataSet.dataSetElements(),
                dataSetElement -> dataSetElement);

        dataSetIndicatorLinkHandler.handleMany(dataSet.uid(), dataSet.indicators(),
                indicator -> DataSetIndicatorLink.builder().dataSet(dataSet.uid()).indicator(indicator.uid()).build());

        if (action == HandleAction.Update) {
            sectionOrphanCleaner.deleteOrphan(dataSet, dataSet.sections());
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<DataSet> dataSets) {
        collectionCleaner.deleteNotPresent(dataSets);
    }
}