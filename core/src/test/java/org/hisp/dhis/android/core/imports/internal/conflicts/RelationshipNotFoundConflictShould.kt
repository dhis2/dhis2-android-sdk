/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.imports.internal.conflicts

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class RelationshipNotFoundConflictShould : BaseConflictShould() {

    private val notFoundConflict = TrackedImportConflictSamples.relationshipNotFound(relationshipUid)
    private val alreadyDeletedConflict = TrackedImportConflictSamples.relationshipAlreadyDeleted(relationshipUid)

    @Test
    fun match_not_found_error_message() {
        assertThat(RelationshipNotFoundConflict.matches(notFoundConflict)).isTrue()
    }

    @Test
    fun match_already_deleted_error_message() {
        assertThat(RelationshipNotFoundConflict.matches(alreadyDeletedConflict)).isTrue()
    }

    @Test
    fun match_relationship_uid_from_not_found() {
        val value = RelationshipNotFoundConflict.getRelationship(notFoundConflict)
        assertThat(value == relationshipUid).isTrue()
    }

    @Test
    fun match_relationship_uid_from_already_deleted() {
        val value = RelationshipNotFoundConflict.getRelationship(alreadyDeletedConflict)
        assertThat(value == relationshipUid).isTrue()
    }

    @Test
    fun create_display_description() = runTest {
        val displayDescription = RelationshipNotFoundConflict.getDisplayDescription(notFoundConflict, context)
        assertThat(displayDescription == "Your relationship $relationshipUid does not exist in the server").isTrue()
    }
}
