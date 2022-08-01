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
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.dataset.DataSetInternalAccessor;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.core.indicator.Indicator;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class DataSetHandler extends IdentifiableHandlerImpl<DataSet> {

    private final Handler<Section> sectionHandler;
    private final OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    private final Handler<DataElementOperand> compulsoryDataElementOperandHandler;
    private final LinkHandler<DataElementOperand, DataSetCompulsoryDataElementOperandLink>
            dataSetCompulsoryDataElementOperandLinkHandler;

    private final LinkHandler<DataInputPeriod, DataInputPeriod> dataInputPeriodHandler;
    private final LinkHandler<DataSetElement, DataSetElement> dataSetElementLinkHandler;
    private final LinkHandler<Indicator, DataSetIndicatorLink> dataSetIndicatorLinkHandler;
    private final CollectionCleaner<DataSet> collectionCleaner;
    private final LinkCleaner<DataSet> linkCleaner;

    @Inject
    DataSetHandler(IdentifiableObjectStore<DataSet> dataSetStore,
                   Handler<Section> sectionHandler,
                   OrphanCleaner<DataSet, Section> sectionOrphanCleaner,
                   Handler<DataElementOperand> compulsoryDataElementOperandHandler,
                   LinkHandler<DataElementOperand, DataSetCompulsoryDataElementOperandLink>
                           dataSetCompulsoryDataElementOperandLinkHandler,
                   LinkHandler<DataInputPeriod, DataInputPeriod> dataInputPeriodHandler,
                   LinkHandler<DataSetElement, DataSetElement> dataSetElementLinkHandler,
                   LinkHandler<Indicator, DataSetIndicatorLink> dataSetIndicatorLinkHandler,
                   CollectionCleaner<DataSet> collectionCleaner, LinkCleaner<DataSet> linkCleaner) {

        super(dataSetStore);
        this.sectionHandler = sectionHandler;
        this.sectionOrphanCleaner = sectionOrphanCleaner;
        this.compulsoryDataElementOperandHandler = compulsoryDataElementOperandHandler;
        this.dataSetCompulsoryDataElementOperandLinkHandler = dataSetCompulsoryDataElementOperandLinkHandler;
        this.dataInputPeriodHandler = dataInputPeriodHandler;
        this.dataSetElementLinkHandler = dataSetElementLinkHandler;
        this.dataSetIndicatorLinkHandler = dataSetIndicatorLinkHandler;
        this.collectionCleaner = collectionCleaner;
        this.linkCleaner = linkCleaner;
    }

    @Override
    protected void afterObjectHandled(final DataSet dataSet, HandleAction action) {

        List<Section> sections = DataSetInternalAccessor.accessSections(dataSet);

        sectionHandler.handleMany(sections);

        compulsoryDataElementOperandHandler.handleMany(dataSet.compulsoryDataElementOperands());

        dataSetCompulsoryDataElementOperandLinkHandler.handleMany(dataSet.uid(),
                dataSet.compulsoryDataElementOperands(),
                dataElementOperand -> DataSetCompulsoryDataElementOperandLink.builder()
                        .dataSet(dataSet.uid()).dataElementOperand(dataElementOperand.uid()).build());

        dataInputPeriodHandler.handleMany(dataSet.uid(),
                dataSet.dataInputPeriods(),
                dataInputPeriod -> dataInputPeriod.toBuilder().dataSet(ObjectWithUid.create(dataSet.uid())).build());

        dataSetElementLinkHandler.handleMany(dataSet.uid(), dataSet.dataSetElements(),
                dataSetElement -> dataSetElement.toBuilder().dataSet(ObjectWithUid.create(dataSet.uid())).build());

        dataSetIndicatorLinkHandler.handleMany(dataSet.uid(), dataSet.indicators(),
                indicator -> DataSetIndicatorLink.builder().dataSet(dataSet.uid()).indicator(indicator.uid()).build());

        if (action == HandleAction.Update) {
            sectionOrphanCleaner.deleteOrphan(dataSet, sections);
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<DataSet> dataSets) {
        collectionCleaner.deleteNotPresent(dataSets);
        linkCleaner.deleteNotPresent(dataSets);
    }
}