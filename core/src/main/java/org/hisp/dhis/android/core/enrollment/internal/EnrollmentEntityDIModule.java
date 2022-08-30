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

package org.hisp.dhis.android.core.enrollment.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.DataOrphanCleanerImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.note.internal.NoteForEnrollmentChildrenAppender;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.internal.EnrollmentRelationshipOrphanCleanerImpl;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class EnrollmentEntityDIModule {

    @Provides
    @Reusable
    public EnrollmentStore store(DatabaseAdapter databaseAdapter) {
        return EnrollmentStoreImpl.create(databaseAdapter);
    }

    @Provides
    @Reusable
    public IdentifiableDataHandler<Enrollment> handler(EnrollmentHandler impl) {
        return impl;
    }

    @Provides
    @Reusable
    Transformer<EnrollmentCreateProjection, Enrollment> transformer() {
        return new EnrollmentProjectionTransformer();
    }

    @Provides
    @Reusable
    @SuppressWarnings("PMD.NonStaticInitializer")
    Map<String, ChildrenAppender<Enrollment>> childrenAppenders(DatabaseAdapter databaseAdapter) {
        return new HashMap<String, ChildrenAppender<Enrollment>>() {{
            put(EnrollmentFields.NOTES, NoteForEnrollmentChildrenAppender.create(databaseAdapter));
        }};
    }

    @Provides
    @Reusable
    OrphanCleaner<Enrollment, Relationship> relationshipOrphanCleaner(EnrollmentRelationshipOrphanCleanerImpl impl) {
        return impl;
    }

    @Provides
    @Reusable
    OrphanCleaner<Enrollment, Event> eventOrphanCleaner(DatabaseAdapter databaseAdapter) {
        return new DataOrphanCleanerImpl<>(EventTableInfo.TABLE_INFO.name(), EventTableInfo.Columns.ENROLLMENT,
                DataColumns.SYNC_STATE, databaseAdapter);
    }
}