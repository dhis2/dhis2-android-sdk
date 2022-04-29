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

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkChildStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandTableInfo;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLinkTableInfo;

final class SectionGreyedFieldsChildrenAppender extends ChildrenAppender<Section> {

    private static final LinkTableChildProjection CHILD_PROJECTION = new LinkTableChildProjection(
            DataElementOperandTableInfo.TABLE_INFO,
            SectionGreyedFieldsLinkTableInfo.Columns.SECTION,
            SectionGreyedFieldsLinkTableInfo.Columns.DATA_ELEMENT_OPERAND);

    private final LinkChildStore<Section, DataElementOperand> linkChildStore;

    private SectionGreyedFieldsChildrenAppender(LinkChildStore<Section, DataElementOperand> linkChildStore) {
        this.linkChildStore = linkChildStore;
    }

    @Override
    public Section appendChildren(Section section) {
        Section.Builder builder = section.toBuilder();
        builder.greyedFields(linkChildStore.getChildren(section));
        return builder.build();
    }

    static ChildrenAppender<Section> create(DatabaseAdapter databaseAdapter) {
        return new SectionGreyedFieldsChildrenAppender(
                StoreFactory.linkChildStore(
                        databaseAdapter,
                        SectionGreyedFieldsLinkTableInfo.TABLE_INFO,
                        CHILD_PROJECTION,
                        DataElementOperand::create
                )
        );
    }
}