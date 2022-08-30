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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleanerImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleanerImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.ParentOrphanCleaner;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo.Columns;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class ProgramEntityDIModule {

    @Provides
    @Reusable
    public ProgramStoreInterface store(DatabaseAdapter databaseAdapter) {
        return ProgramStore.create(databaseAdapter);
    }

    @Provides
    @Reusable
    public Handler<Program> handler(ProgramHandler impl) {
        return impl;
    }

    @Provides
    @Reusable
    @SuppressWarnings("PMD.NonStaticInitializer")
    Map<String, ChildrenAppender<Program>> childrenAppenders(
            DatabaseAdapter databaseAdapter,
            ProgramTrackedEntityTypeChildrenAppender trackedEntityTypeChildrenAppender) {

        return new HashMap<String, ChildrenAppender<Program>>() {{
            put(Columns.TRACKED_ENTITY_TYPE, trackedEntityTypeChildrenAppender);
        }};
    }

    @Provides
    @Reusable
    public CollectionCleaner<Program> collectionCleaner(DatabaseAdapter databaseAdapter) {
        return new CollectionCleanerImpl<>(ProgramTableInfo.TABLE_INFO.name(), databaseAdapter);
    }

    @Provides
    @Reusable
    public LinkCleaner<Program> linkCleaner(ProgramStoreInterface programStore,
                                            DatabaseAdapter databaseAdapter) {
        return new LinkCleanerImpl<>(OrganisationUnitProgramLinkTableInfo.TABLE_INFO.name(),
                OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM, programStore, databaseAdapter);
    }

    @Provides
    @Reusable
    ParentOrphanCleaner<Program> parentOrphanCleaner(DatabaseAdapter databaseAdapter) {
        return ProgramOrphanCleaner.create(databaseAdapter);
    }
}