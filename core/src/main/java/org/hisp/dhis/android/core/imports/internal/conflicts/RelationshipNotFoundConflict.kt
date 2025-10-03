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

import org.hisp.dhis.android.core.imports.internal.ImportConflict

internal object RelationshipNotFoundConflict : TrackerImportConflictItem {

    private val notFoundRegex: Regex = Regex("Relationship '(\\w{11})' not found\\.")
    private val alreadyDeletedRegex: Regex = Regex(
        "Relationship '(\\w{11})' is already deleted and cannot be modified\\."
    )

    private fun description(relationshipUid: String) =
        "Your relationship $relationshipUid does not exist in the server"

    override val errorCode: String = "E4005"

    override fun matches(conflict: ImportConflict): Boolean {
        return matchesString(conflict.value())
    }

    /**
     * Checks if the given string matches the relationship not found or already deleted patterns.
     * This is used by both conflict matching and description field checking.
     */
    fun matchesString(value: String): Boolean {
        return notFoundRegex.matches(value) || alreadyDeletedRegex.matches(value)
    }

    fun getRelationship(conflict: ImportConflict): String? {
        return notFoundRegex.find(conflict.value())?.groupValues?.get(1)
            ?: alreadyDeletedRegex.find(conflict.value())?.groupValues?.get(1)
    }

    override suspend fun getDisplayDescription(
        conflict: ImportConflict,
        context: TrackerImportConflictItemContext,
    ): String {
        return getRelationship(conflict)?.let { relationshipUid ->
            description(relationshipUid)
        }
            ?: conflict.value()
    }
}