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

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.CollectionCleaner;
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
import org.hisp.dhis.android.core.indicator.Indicator;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class DataSetHandler extends IdentifiableSyncHandlerImpl<DataSet> {

    private final SyncHandlerWithTransformer<ObjectStyle> styleHandler;

    private final SyncHandler<Section> sectionHandler;
    private final OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    private final SyncHandler<DataElementOperand> compulsoryDataElementOperandHandler;
    private final LinkModelHandler<DataElementOperand,
            DataSetCompulsoryDataElementOperandLinkModel> dataSetCompulsoryDataElementOperandLinkHandler;

    private final LinkSyncHandlerWithTransformer<DataInputPeriod> dataInputPeriodHandler;
    private final LinkSyncHandler<DataSetElement> dataSetElementLinkHandler;
    private final LinkModelHandler<Indicator, DataSetIndicatorLinkModel> dataSetIndicatorLinkHandler;
    private final CollectionCleaner<DataSet> collectionCleaner;

    @Inject
    DataSetHandler(IdentifiableObjectStore<DataSet> dataSetStore,
                   SyncHandlerWithTransformer<ObjectStyle> styleHandler,
                   SyncHandler<Section> sectionHandler,
                   OrphanCleaner<DataSet, Section> sectionOrphanCleaner,
                   SyncHandler<DataElementOperand> compulsoryDataElementOperandHandler,
                   LinkModelHandler<DataElementOperand,
                           DataSetCompulsoryDataElementOperandLinkModel>
                           dataSetCompulsoryDataElementOperandLinkHandler,
                   LinkSyncHandlerWithTransformer<DataInputPeriod> dataInputPeriodHandler,
                   LinkSyncHandler<DataSetElement> dataSetElementLinkHandler,
                   LinkModelHandler<Indicator, DataSetIndicatorLinkModel> dataSetIndicatorLinkHandler,
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

        styleHandler.handle(dataSet.style(),
                new ObjectStyleModelBuilder(dataSet.uid(), DataSetModel.TABLE));

        sectionHandler.handleMany(dataSet.sections());

        compulsoryDataElementOperandHandler.handleMany(dataSet.compulsoryDataElementOperands());

        dataSetCompulsoryDataElementOperandLinkHandler.handleMany(dataSet.uid(),
                dataSet.compulsoryDataElementOperands(),
                new DataSetCompulsoryDataElementOperandLinkModelBuilder(dataSet));

        dataInputPeriodHandler.handleMany(dataSet.uid(),
                dataSet.dataInputPeriods(),
                new ModelBuilder<DataInputPeriod, DataInputPeriod>() {
            @Override
            public DataInputPeriod buildModel(DataInputPeriod dataInputPeriod) {
                return dataInputPeriod.toBuilder().dataSet(ObjectWithUid.create(dataSet.uid())).build();
            }
        });

        dataSetElementLinkHandler.handleMany(dataSet.uid(), dataSet.dataSetElements());

        dataSetIndicatorLinkHandler.handleMany(dataSet.uid(), dataSet.indicators(),
                new DataSetIndicatorLinkModelBuilder(dataSet));

        if (action == HandleAction.Update) {
            sectionOrphanCleaner.deleteOrphan(dataSet, dataSet.sections());
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<DataSet> dataSets) {
        collectionCleaner.deleteNotPresent(dataSets);
    }
}