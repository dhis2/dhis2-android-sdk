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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandHandler;
import org.hisp.dhis.android.core.dataelement.DataElementOperandModel;
import org.hisp.dhis.android.core.dataelement.DataElementOperandModelBuilder;

public class SectionHandler extends IdentifiableHandlerImpl<Section, SectionModel> {

    private final LinkModelHandler<ObjectWithUid, SectionDataElementLinkModel> sectionDataElementLinkHandler;

    private final GenericHandler<DataElementOperand, DataElementOperandModel> greyedFieldsHandler;
    private final LinkModelHandler<DataElementOperand, SectionGreyedFieldsLinkModel> sectionGreyedFieldsLinkHandler;

    SectionHandler(IdentifiableObjectStore<SectionModel> sectionStore,
                   LinkModelHandler<ObjectWithUid, SectionDataElementLinkModel> sectionDataElementLinkHandler,
                   GenericHandler<DataElementOperand, DataElementOperandModel> greyedFieldsHandler,
                   LinkModelHandler<DataElementOperand, SectionGreyedFieldsLinkModel> sectionGreyedFieldsLinkHandler) {

        super(sectionStore);

        this.sectionDataElementLinkHandler = sectionDataElementLinkHandler;
        this.greyedFieldsHandler = greyedFieldsHandler;
        this.sectionGreyedFieldsLinkHandler = sectionGreyedFieldsLinkHandler;
    }

    public static SectionHandler create(DatabaseAdapter databaseAdapter) {
        return new SectionHandler(
                SectionStore.create(databaseAdapter),
                new LinkModelHandlerImpl<ObjectWithUid,
                        SectionDataElementLinkModel>(SectionDataElementLinkStore.create(databaseAdapter)),
                DataElementOperandHandler.create(databaseAdapter),
                new LinkModelHandlerImpl<DataElementOperand, SectionGreyedFieldsLinkModel>(
                        SectionGreyedFieldsLinkStore.create(databaseAdapter)));
    }

    @Override
    protected void afterObjectHandled(Section section, HandleAction action) {

        greyedFieldsHandler.handleMany(section.greyedFields(), new DataElementOperandModelBuilder());

        sectionDataElementLinkHandler.handleMany(section.uid(),
                section.dataElements(),
                new SectionDataElementLinkModelBuilder(section));

        sectionGreyedFieldsLinkHandler.handleMany(section.uid(),
                section.greyedFields(),
                new SectionGreyedFieldsLinkModelBuilder(section));
        }
}