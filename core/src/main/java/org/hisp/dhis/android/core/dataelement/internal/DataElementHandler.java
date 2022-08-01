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
package org.hisp.dhis.android.core.dataelement.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeValueUtils;
import org.hisp.dhis.android.core.attribute.DataElementAttributeValueLink;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.legendset.DataElementLegendSetLink;


import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class DataElementHandler extends IdentifiableHandlerImpl<DataElement> {
    private final Handler<Attribute> attributeHandler;
    private final LinkHandler<Attribute, DataElementAttributeValueLink>
            dataElementAttributeLinkHandler;
    private final OrderedLinkHandler<ObjectWithUid, DataElementLegendSetLink> dataElementLegendSetLinkHandler;

    @Inject
    DataElementHandler(
            IdentifiableObjectStore<DataElement> programStageDataElementStore,
            Handler<Attribute> attributeHandler,
            LinkHandler<Attribute, DataElementAttributeValueLink> dataElementAttributeLinkHandler,
            OrderedLinkHandler<ObjectWithUid, DataElementLegendSetLink> dataElementLegendSetLinkHandler
    ) {
        super(programStageDataElementStore);
        this.attributeHandler = attributeHandler;
        this.dataElementAttributeLinkHandler = dataElementAttributeLinkHandler;
        this.dataElementLegendSetLinkHandler = dataElementLegendSetLinkHandler;
    }

    @Override
    protected void afterObjectHandled(DataElement dataElement, HandleAction action) {
        if (dataElement.attributeValues() != null) {
            final List<Attribute> attributes = AttributeValueUtils.extractAttributes(dataElement.attributeValues());

            attributeHandler.handleMany(attributes);

            dataElementAttributeLinkHandler.handleMany(dataElement.uid(), attributes,
                    attribute -> DataElementAttributeValueLink.builder()
                            .dataElement(dataElement.uid())
                            .attribute(attribute.uid())
                            .value(AttributeValueUtils.extractValue(dataElement.attributeValues(), attribute.uid()))
                            .build());
        }

        if (dataElement.legendSets() != null) {
            dataElementLegendSetLinkHandler.handleMany(dataElement.uid(), dataElement.legendSets(),
                    (legendSet, sortOrder) -> DataElementLegendSetLink.builder()
                            .dataElement(dataElement.uid())
                            .legendSet(legendSet.uid())
                            .sortOrder(sortOrder)
                            .build());
        }
    }
}
