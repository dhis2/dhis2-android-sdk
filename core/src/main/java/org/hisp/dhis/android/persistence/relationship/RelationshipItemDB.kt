package org.hisp.dhis.android.persistence.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEnrollment
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.event.EventDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceDB

@Entity(
    tableName = "RelationshipItem",
    foreignKeys = [
        ForeignKey(
            entity = RelationshipDB::class,
            parentColumns = ["uid"],
            childColumns = ["relationship"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["relationship", "relationshipItemType"],
)
internal data class RelationshipItemDB(
    val relationship: String,
    val relationshipItemType: String,
    val trackedEntityInstance: String?,
    val enrollment: String?,
    val event: String?,
) : EntityDB<RelationshipItem> {

    override fun toDomain(): RelationshipItem {
        return RelationshipItem.builder().apply {
            relationship(ObjectWithUidDB(relationship).toDomain())
            relationshipItemType(RelationshipConstraintType.valueOf(relationshipItemType))
            trackedEntityInstance(
                RelationshipItemTrackedEntityInstance.builder()
                    .trackedEntityInstance(trackedEntityInstance)
                    .build(),
            )
            enrollment?.let { enrollment(RelationshipItemEnrollment.builder().enrollment(it).build()) }
            event?.let { event(RelationshipItemEvent.builder().event(it).build()) }
        }

            .build()
    }
}

internal fun RelationshipItem.toDB(): RelationshipItemDB {
    return RelationshipItemDB(
        relationship = relationship()?.uid()!!,
        relationshipItemType = relationshipItemType()?.name!!,
        trackedEntityInstance = trackedEntityInstance()?.trackedEntityInstance(),
        enrollment = enrollment()?.enrollment(),
        event = event()?.event(),
    )
}
