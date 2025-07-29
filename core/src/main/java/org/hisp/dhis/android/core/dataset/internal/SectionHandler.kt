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
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandHandler
import org.hisp.dhis.android.core.dataset.Section
import org.hisp.dhis.android.core.dataset.SectionDataElementLink
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLink
import org.koin.core.annotation.Singleton

@Singleton
internal class SectionHandler(
    sectionStore: SectionStore,
    private val sectionDataElementLinkHandler: SectionDataElementLinkHandler,
    private val greyedFieldsHandler: DataElementOperandHandler,
    private val sectionGreyedFieldsLinkHandler: SectionGreyedFieldsLinkHandler,
    private val sectionIndicatorLinkHandler: SectionIndicatorLinkHandler,
    private val sectionGreyedFieldsStore: SectionGreyedFieldsLinkStore,
) : IdentifiableHandlerImpl<Section>(sectionStore) {

    override suspend fun afterObjectHandled(o: Section, action: HandleAction) {
        sectionDataElementLinkHandler.handleMany(
            o.uid(),
            o.dataElements(),
        ) { dataElement: DataElement, sortOrder: Int ->
            SectionDataElementLink.builder()
                .section(o.uid())
                .dataElement(dataElement.uid())
                .sortOrder(sortOrder)
                .build()
        }

        sectionIndicatorLinkHandler.handleMany(
            o.uid(),
            o.indicators(),
        ) {
            SectionIndicatorLink.builder()
                .section(o.uid())
                .indicator(it.uid())
                .build()
        }

        greyedFieldsHandler.handleMany(o.greyedFields())

        sectionGreyedFieldsStore.deleteBySection(o.uid())

        sectionGreyedFieldsLinkHandler.handleMany(
            o.uid(),
            o.greyedFields(),
        ) { dataElementOperand: DataElementOperand ->
            SectionGreyedFieldsLink.builder()
                .section(o.uid())
                .dataElementOperand(dataElementOperand.uid())
                .categoryOptionCombo(getUidOrNull(dataElementOperand.categoryOptionCombo()))
                .build()
        }
    }
}
