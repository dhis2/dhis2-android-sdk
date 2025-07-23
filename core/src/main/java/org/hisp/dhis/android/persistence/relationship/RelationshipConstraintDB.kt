package org.hisp.dhis.android.persistence.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.relationship.RelationshipConstraint
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.core.relationship.TrackerDataView
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeDB

@Entity(
    tableName = "RelationshipConstraint",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["relationshipType", "constraintType"],
)
internal data class RelationshipConstraintDB(
    val relationshipType: String,
    val constraintType: String,
    val relationshipEntity: String?,
    val trackedEntityType: String?,
    val program: String?,
    val programStage: String?,
    val trackerDataViewAttributes: StringListDB?,
    val trackerDataViewDataElements: StringListDB?,
) : EntityDB<RelationshipConstraint> {

    override fun toDomain(): RelationshipConstraint {
        return RelationshipConstraint.builder()
            .relationshipType(ObjectWithUidDB(relationshipType).toDomain())
            .constraintType(RelationshipConstraintType.valueOf(constraintType))
            .relationshipEntity(relationshipEntity?.let { RelationshipEntityType.valueOf(it) })
            .trackedEntityType(trackedEntityType?.let { ObjectWithUidDB(it).toDomain() })
            .program(program?.let { ObjectWithUidDB(it).toDomain() })
            .programStage(programStage?.let { ObjectWithUidDB(it).toDomain() })
            .trackerDataView(
                TrackerDataView.builder()
                    .attributes(trackerDataViewAttributes?.toDomain())
                    .dataElements(trackerDataViewDataElements?.toDomain())
                    .build(),
            )
            .build()
    }
}

internal fun RelationshipConstraint.toDB(): RelationshipConstraintDB {
    return RelationshipConstraintDB(
        relationshipType = relationshipType()!!.uid(),
        constraintType = constraintType()!!.name,
        relationshipEntity = relationshipEntity()?.name,
        trackedEntityType = trackedEntityType()?.uid(),
        program = program()?.uid(),
        programStage = programStage()?.uid(),
        trackerDataViewAttributes = trackerDataView()?.attributes()?.toDB(),
        trackerDataViewDataElements = trackerDataView()?.dataElements()?.toDB(),
    )
}
