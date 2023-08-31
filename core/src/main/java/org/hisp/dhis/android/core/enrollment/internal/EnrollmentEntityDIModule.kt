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
package org.hisp.dhis.android.core.enrollment.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.internal.EventHandler
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager
import org.hisp.dhis.android.core.note.internal.NoteForEnrollmentChildrenAppender
import org.hisp.dhis.android.core.note.internal.NoteHandler
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.relationship.internal.EnrollmentRelationshipOrphanCleaner
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler

@Module
internal class EnrollmentEntityDIModule {
    @Provides
    @Reusable
    fun store(databaseAdapter: DatabaseAdapter): EnrollmentStore {
        return EnrollmentStoreImpl(databaseAdapter)
    }

    @Provides
    @Reusable
    @Suppress("LongParameterList")
    fun handler(
        relationshipVersionManager: RelationshipDHISVersionManager,
        relationshipHandler: RelationshipHandler,
        noteVersionManager: NoteDHISVersionManager,
        enrollmentStore: EnrollmentStore,
        eventHandler: EventHandler,
        eventOrphanCleaner: EventOrphanCleaner,
        noteHandler: NoteHandler,
        noteUniquenessManager: NoteUniquenessManager,
        relationshipOrphanCleaner: EnrollmentRelationshipOrphanCleaner,
    ): EnrollmentHandler {
        return EnrollmentHandler(
            relationshipVersionManager,
            relationshipHandler,
            noteVersionManager,
            enrollmentStore,
            eventHandler,
            eventOrphanCleaner,
            noteHandler,
            noteUniquenessManager,
            relationshipOrphanCleaner,
        )
    }

    @Provides
    @Reusable
    fun childrenAppenders(databaseAdapter: DatabaseAdapter): Map<String, ChildrenAppender<Enrollment>> {
        return mapOf(
            EnrollmentFields.NOTES to NoteForEnrollmentChildrenAppender.create(databaseAdapter),
        )
    }
}
