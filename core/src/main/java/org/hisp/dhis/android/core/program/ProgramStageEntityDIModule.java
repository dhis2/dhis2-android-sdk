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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.di.internal.IdentifiableStoreProvider;
import org.hisp.dhis.android.core.arch.handlers.internal.SyncHandler;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleanerImpl;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyleChildrenAppender;
import org.hisp.dhis.android.core.common.ObjectStyleStoreImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleanerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class ProgramStageEntityDIModule implements IdentifiableStoreProvider<ProgramStage> {

    @Override
    @Provides
    @Reusable
    public IdentifiableObjectStore<ProgramStage> store(DatabaseAdapter databaseAdapter) {
        return ProgramStageStore.create(databaseAdapter);
    }

    @Provides
    @Reusable
    public SyncHandler<ProgramStage> handler(ProgramStageHandler impl) {
        return impl;
    }

    @Provides
    @Reusable
    public OrphanCleaner<ProgramStage, ProgramStageDataElement> dataElementOrphanCleaner(
            DatabaseAdapter databaseAdapter) {
        return new OrphanCleanerImpl<>(ProgramStageDataElementTableInfo.TABLE_INFO.name(),
                ProgramStageDataElementFields.PROGRAM_STAGE, databaseAdapter);
    }

    @Provides
    @Reusable
    public OrphanCleaner<ProgramStage, ProgramStageSection> sectionOrphanCleaner(DatabaseAdapter databaseAdapter) {
        return new OrphanCleanerImpl<>(ProgramStageSectionTableInfo.TABLE_INFO.name(),
                        ProgramStageSectionTableInfo.Columns.PROGRAM_STAGE, databaseAdapter);
    }

    @Provides
    @Reusable
    public CollectionCleaner<ProgramStage> collectionCleaner(DatabaseAdapter databaseAdapter) {
        return new CollectionCleanerImpl<>(ProgramStageTableInfo.TABLE_INFO.name(), databaseAdapter);
    }

    @Provides
    @Reusable
    @SuppressWarnings("PMD.NonStaticInitializer")
    Map<String, ChildrenAppender<ProgramStage>> childrenAppenders(DatabaseAdapter databaseAdapter) {
        ChildrenAppender<ProgramStage> objectStyleChildrenAppender =
                new ObjectStyleChildrenAppender<>(
                        ObjectStyleStoreImpl.create(databaseAdapter),
                        ProgramStageTableInfo.TABLE_INFO
                );

        return new HashMap<String, ChildrenAppender<ProgramStage>>() {{
            put(ProgramStageFields.STYLE, objectStyleChildrenAppender);
            put(ProgramStageFields.PROGRAM_STAGE_DATA_ELEMENTS,
                    ProgramStageDataElementChildrenAppender.create(databaseAdapter));
            put(ProgramStageFields.PROGRAM_STAGE_SECTIONS, ProgramStageSectionChildrenAppender.create(databaseAdapter));
        }};
    }
}