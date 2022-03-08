/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.program.ProgramSectionTableInfo.Columns;
import org.hisp.dhis.android.core.program.internal.ProgramSectionFields;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramSectionCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramSection, ProgramSectionCollectionRepository> {

    @Inject
    ProgramSectionCollectionRepository(final IdentifiableObjectStore<ProgramSection> store,
                                       final Map<String, ChildrenAppender<ProgramSection>> childrenAppenders,
                                       final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramSectionCollectionRepository(store, childrenAppenders, s)));
    }


    public StringFilterConnector<ProgramSectionCollectionRepository> byDescription() {
        return cf.string(Columns.DESCRIPTION);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byProgramUid() {
        return cf.string(Columns.PROGRAM);
    }

    public IntegerFilterConnector<ProgramSectionCollectionRepository> bySortOrder() {
        return cf.integer(Columns.SORT_ORDER);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byFormName() {
        return cf.string(Columns.FORM_NAME);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byColor() {
        return cf.string(Columns.COLOR);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byIcon() {
        return cf.string(Columns.ICON);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byDesktopRenderType() {
        return cf.string(ProgramSectionTableInfo.Columns.DESKTOP_RENDER_TYPE);
    }

    public StringFilterConnector<ProgramSectionCollectionRepository> byMobileRenderType() {
        return cf.string(ProgramSectionTableInfo.Columns.MOBILE_RENDER_TYPE);
    }

    public ProgramSectionCollectionRepository withAttributes() {
        return cf.withChild(ProgramSectionFields.ATTRIBUTES);
    }
}