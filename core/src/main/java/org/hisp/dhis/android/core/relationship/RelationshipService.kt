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

package org.hisp.dhis.android.core.relationship

interface RelationshipService {
    fun hasAccessPermission(relationshipType: RelationshipType): Boolean

    /**
     * Returns all the relationship types relevant to the given trackedEntityType and program (optional).
     * The list includes the side of the TrackedEntity (FROM or TO). If the TrackedEntity might be in both sides,
     * the list will include two entries for the same relationship type, one for each side.
     *
     * @param trackedEntityType the trackedEntityType uid
     * @param programUid optional program uid
     */
    fun getRelationshipTypesForTrackedEntities(
        trackedEntityType: String,
        programUid: String? = null,
    ): List<RelationshipTypeWithEntitySide>

    /**
     * Returns all the relationship types relevant to the given enrollment (program uid).
     * The list includes the side of the Enrollment (FROM or TO). If the Enrollment might be in both sides,
     * the list will include two entries for the same relationship type, one for each side.
     *
     * @param programUid the program uid
     */
    fun getRelationshipTypesForEnrollments(
        programUid: String,
    ): List<RelationshipTypeWithEntitySide>

    /**
     * Returns all the relationship types relevant to the given event (program stage uid).
     * The list includes the side of the Event (FROM or TO). If the Event might be in both sides,
     * the list will include two entries for the same relationship type, one for each side.
     *
     * @param programStageUid the program stage uid
     */
    fun getRelationshipTypesForEvents(
        programStageUid: String,
    ): List<RelationshipTypeWithEntitySide>
}
