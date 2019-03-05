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
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkSyncHandler;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class SectionHandler extends IdentifiableSyncHandlerImpl<Section> {

    private final OrderedLinkSyncHandler<DataElement, SectionDataElementLink> sectionDataElementLinkHandler;
    private final SyncHandler<DataElementOperand> greyedFieldsHandler;
    private final LinkSyncHandler<SectionGreyedFieldsLink> sectionGreyedFieldsLinkHandler;

    @Inject
    SectionHandler(IdentifiableObjectStore<Section> sectionStore,
                   OrderedLinkSyncHandler<DataElement, SectionDataElementLink> sectionDataElementLinkHandler,
                   SyncHandler<DataElementOperand> greyedFieldsHandler,
                   LinkSyncHandler<SectionGreyedFieldsLink> sectionGreyedFieldsLinkHandler) {

        super(sectionStore);
        this.sectionDataElementLinkHandler = sectionDataElementLinkHandler;
        this.greyedFieldsHandler = greyedFieldsHandler;
        this.sectionGreyedFieldsLinkHandler = sectionGreyedFieldsLinkHandler;
    }

    @Override
    protected void afterObjectHandled(Section section, HandleAction action) {
        sectionDataElementLinkHandler.handleMany(section.uid(), section.dataElements(),
                (dataElement, sortOrder) -> SectionDataElementLink.builder()
                        .section(section.uid())
                        .dataElement(dataElement.uid())
                        .sortOrder(sortOrder)
                        .build());

        if (section.greyedFields() != null) {
            greyedFieldsHandler.handleMany(section.greyedFields());

            List<SectionGreyedFieldsLink> sectionGreyedFieldsLinks = new ArrayList<>();
            for (DataElementOperand dataElementOperand : section.greyedFields()) {
                sectionGreyedFieldsLinks.add(SectionGreyedFieldsLink.builder()
                        .section(section.uid()).dataElementOperand(dataElementOperand.uid()).build());
            }
            sectionGreyedFieldsLinkHandler.handleMany(section.uid(), sectionGreyedFieldsLinks);
        }
    }
}