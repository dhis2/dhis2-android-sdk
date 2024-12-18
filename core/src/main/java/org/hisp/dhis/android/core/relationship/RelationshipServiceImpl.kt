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

import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipServiceImpl(
    private val programRepository: ProgramCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val trackedEntityTypeRepository: TrackedEntityTypeCollectionRepository,
    private val relationshipTypeRepository: RelationshipTypeCollectionRepository,
) : RelationshipService {
    override fun hasAccessPermission(relationshipType: RelationshipType): Boolean {
        val fromAccess = relationshipType.fromConstraint()?.let { constraintAccess(it) } ?: false
        val toAccess = relationshipType.toConstraint()?.let { constraintAccess(it) } ?: false

        val writeAccess = if (relationshipType.bidirectional() == true) {
            fromAccess && toAccess
        } else {
            fromAccess
        }

        return writeAccess
    }

    override fun getRelationshipTypesForTrackedEntities(
        trackedEntityType: String,
        programUid: String?,
    ): List<RelationshipTypeWithEntitySide> {
        val entityType = RelationshipEntityType.TRACKED_ENTITY_INSTANCE

        val potentialRelTypes = relationshipTypeRepository
            .byConstraint(entityType, trackedEntityType)
            .withConstraints()
            .blockingGet()

        return mapToApplicableSides(entityType, potentialRelTypes) { constraint ->
            constraint.trackedEntityType()?.uid() == trackedEntityType &&
                (
                    programUid == null ||
                        constraint.program()?.uid() == null ||
                        constraint.program()?.uid() == programUid
                    )
        }
    }

    override fun getRelationshipTypesForEnrollments(
        programUid: String,
    ): List<RelationshipTypeWithEntitySide> {
        val entityType = RelationshipEntityType.PROGRAM_INSTANCE

        val potentialRelTypes = relationshipTypeRepository
            .byConstraint(entityType, programUid)
            .withConstraints()
            .blockingGet()

        return mapToApplicableSides(entityType, potentialRelTypes) { constraint ->
            constraint.program()?.uid() == programUid
        }
    }

    override fun getRelationshipTypesForEvents(
        programStageUid: String,
    ): List<RelationshipTypeWithEntitySide> {
        val entityType = RelationshipEntityType.PROGRAM_STAGE_INSTANCE

        val potentialRelTypes = relationshipTypeRepository
            .byConstraint(entityType, programStageUid)
            .withConstraints()
            .blockingGet()

        return mapToApplicableSides(entityType, potentialRelTypes) { constraint ->
            constraint.programStage()?.uid() == programStageUid
        }
    }

    private fun constraintAccess(
        constraint: RelationshipConstraint,
    ): Boolean = when (constraint.relationshipEntity()) {
        RelationshipEntityType.PROGRAM_INSTANCE -> {
            val programUid = constraint.program()?.uid()
            programRepository.uid(programUid).blockingGet()!!.access().data().write()!!
        }

        RelationshipEntityType.PROGRAM_STAGE_INSTANCE -> {
            if (constraint.programStage()?.uid() != null) {
                val programStageUid = constraint.programStage()?.uid()
                programStageRepository.uid(programStageUid).blockingGet()?.access()!!.data().write()
            } else {
                val programUid = constraint.program()?.uid()
                programRepository.uid(programUid).blockingGet()!!.access().data().write()!!
            }
        }

        RelationshipEntityType.TRACKED_ENTITY_INSTANCE -> {
            val teTypeUid = constraint.trackedEntityType()?.uid()
            trackedEntityTypeRepository.uid(teTypeUid).blockingGet()!!.access().data().write()!!
        }

        else -> false
    }

    private fun mapToApplicableSides(
        entityType: RelationshipEntityType,
        relationshipTypes: List<RelationshipType>,
        matchesSide: (RelationshipConstraint) -> Boolean,
    ): List<RelationshipTypeWithEntitySide> {
        return relationshipTypes.flatMap { relType ->
            val applicableSides = mutableListOf<RelationshipTypeWithEntitySide>()
            if (matchesConstraint(relType.fromConstraint(), entityType, matchesSide)) {
                applicableSides.add(RelationshipTypeWithEntitySide(relType, RelationshipConstraintType.FROM))
            }
            if (relType.bidirectional() == true && matchesConstraint(relType.toConstraint(), entityType, matchesSide)) {
                applicableSides.add(RelationshipTypeWithEntitySide(relType, RelationshipConstraintType.TO))
            }
            applicableSides
        }
    }

    private fun matchesConstraint(
        constraint: RelationshipConstraint?,
        entityType: RelationshipEntityType,
        matchesSide: (RelationshipConstraint) -> Boolean,
    ): Boolean {
        return constraint?.let {
            constraint.relationshipEntity() == entityType && matchesSide(constraint)
        } ?: false
    }
}
