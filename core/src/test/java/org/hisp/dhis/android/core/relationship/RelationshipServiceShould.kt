package org.hisp.dhis.android.core.relationship

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturnConsecutively
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RelationshipServiceShould {

    private val programRepository: ProgramCollectionRepository = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val programStageRepository: ProgramStageCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val trackedEntityTypeRepository: TrackedEntityTypeCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private lateinit var relationshipService: RelationshipService

    @Before
    fun setUp() {
        relationshipService = RelationshipServiceImpl(
            programRepository,
            programStageRepository,
            trackedEntityTypeRepository
        )
    }

    @Test
    fun `HasAccessPermission should return true if bidirectional and from and to access is true`() {
        val bidirectionalRelationship = relationshipType(
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            true
        )

        mockTeTypeAccessResponse(fromAccess = true, toAccess = true)
        assertTrue(relationshipService.hasAccessPermission(bidirectionalRelationship))
    }

    @Test
    fun `HasAccessPermission should return false if bidirectional and from or to access is false`() {
        val bidirectionalRelationship = relationshipType(
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            true
        )

        mockTeTypeAccessResponse(fromAccess = false, toAccess = true)
        assertTrue(!relationshipService.hasAccessPermission(bidirectionalRelationship))

        mockTeTypeAccessResponse(fromAccess = true, toAccess = false)
        assertTrue(!relationshipService.hasAccessPermission(bidirectionalRelationship))
    }

    @Test
    fun `HasAccessPermission should call correct repository`() {
        val teTypeRelationship = relationshipType(
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            true
        )

        val programRelationship = relationshipType(
            RelationshipEntityType.PROGRAM_INSTANCE,
            RelationshipEntityType.PROGRAM_INSTANCE,
            true
        )

        val programStageRelationship = relationshipType(
            RelationshipEntityType.PROGRAM_STAGE_INSTANCE,
            RelationshipEntityType.PROGRAM_STAGE_INSTANCE,
            true
        )

        mockTeTypeAccessResponse(fromAccess = true, toAccess = true)
        relationshipService.hasAccessPermission(teTypeRelationship)
        verify(trackedEntityTypeRepository, times(2)).uid(any())

        mockProgramAccessResponse(fromAccess = true, toAccess = true)
        relationshipService.hasAccessPermission(programRelationship)
        verify(programRepository, times(2)).uid(any())

        mockProgramStageAccessResponse(fromAccess = true, toAccess = true)
        relationshipService.hasAccessPermission(programStageRelationship)
        verify(programStageRepository, times(2)).uid(any())
    }

    @Test
    fun `HasAccessPermission should return true if from access is true`() {
        val unidirectionalRelationship = relationshipType(
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            false
        )
        mockTeTypeAccessResponse(fromAccess = true, toAccess = false)

        assertTrue(relationshipService.hasAccessPermission(unidirectionalRelationship))
    }

    @Test
    fun `HasAccessPermission should false if from access is false`() {
        val unidirectionalRelationship = relationshipType(
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            RelationshipEntityType.TRACKED_ENTITY_INSTANCE,
            false
        )
        mockTeTypeAccessResponse(fromAccess = false, toAccess = false)

        assertTrue(!relationshipService.hasAccessPermission(unidirectionalRelationship))
    }

    private fun mockTeTypeAccessResponse(fromAccess: Boolean, toAccess: Boolean) {
        whenever(
            trackedEntityTypeRepository.uid(any()).blockingGet()
        ) doReturnConsecutively listOf(
            TrackedEntityType.builder().uid("from").access(accessData(fromAccess)).build(),
            TrackedEntityType.builder().uid("to").access(accessData(toAccess)).build()
        )
    }

    private fun mockProgramAccessResponse(fromAccess: Boolean, toAccess: Boolean) {
        whenever(
            programRepository.uid(any()).blockingGet()
        ) doReturnConsecutively listOf(
            Program.builder().uid("from").access(accessData(fromAccess)).build(),
            Program.builder().uid("to").access(accessData(toAccess)).build()
        )
    }

    private fun mockProgramStageAccessResponse(fromAccess: Boolean, toAccess: Boolean) {
        whenever(
            programStageRepository.uid(any()).blockingGet()
        ) doReturnConsecutively listOf(
            ProgramStage.builder().uid("from").access(accessData(fromAccess)).build(),
            ProgramStage.builder().uid("to").access(accessData(toAccess)).build()
        )
    }

    private fun relationshipType(
        fromType: RelationshipEntityType,
        toType: RelationshipEntityType,
        bidirectional: Boolean
    ) = RelationshipType.builder()
        .uid("relationshipType")
        .fromToName("from")
        .toFromName("to")
        .fromConstraint(
            relationshipConstraint(
                RelationshipConstraintType.FROM,
                fromType
            )
        )
        .toConstraint(
            relationshipConstraint(
                RelationshipConstraintType.TO,
                toType
            )
        )
        .bidirectional(bidirectional)
        .build()

    private fun relationshipConstraint(
        relationshipConstraintType: RelationshipConstraintType,
        relationshipEntityType: RelationshipEntityType
    ) = RelationshipConstraint.builder()
        .relationshipType(ObjectWithUid.create("${relationshipConstraintType.name}_relationshipType"))
        .constraintType(relationshipConstraintType)
        .relationshipEntity(relationshipEntityType)
        .apply {
            when (relationshipEntityType) {
                RelationshipEntityType.PROGRAM_INSTANCE -> {
                    program(ObjectWithUid.create("${relationshipConstraintType.name}_program"))
                }
                RelationshipEntityType.PROGRAM_STAGE_INSTANCE -> {
                    programStage(ObjectWithUid.create("${relationshipConstraintType.name}_programStage"))
                }
                RelationshipEntityType.TRACKED_ENTITY_INSTANCE -> {
                    trackedEntityType(ObjectWithUid.create("${relationshipConstraintType.name}_trackedEntityType"))
                }
            }
        }
        .build()

    private fun accessData(hasAccess: Boolean) = Access.builder()
        .read(true)
        .write(true)
        .data(DataAccess.create(true, hasAccess))
        .build()
}
