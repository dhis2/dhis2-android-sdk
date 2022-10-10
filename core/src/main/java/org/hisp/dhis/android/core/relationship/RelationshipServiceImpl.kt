package org.hisp.dhis.android.core.relationship

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository

@Reusable
internal class RelationshipServiceImpl @Inject constructor(
    private val programRepository: ProgramCollectionRepository,
    private val programStageRepository: ProgramStageCollectionRepository,
    private val trackedEntityTypeRepository: TrackedEntityTypeCollectionRepository
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

    private fun constraintAccess(
        constraint: RelationshipConstraint
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
            trackedEntityTypeRepository.uid(teTypeUid).blockingGet().access().data().write()
        }
        else -> false
    }
}
