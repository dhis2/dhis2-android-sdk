package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "TrackedEntityInstanceFilter",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
    ],
)
internal data class TrackedEntityInstanceFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val color: String?,
    val icon: String?,
    val program: String,
    val description: String?,
    val sortOrder: Int?,
    val enrollmentStatus: String?,
    val followUp: Int?,
    val organisationUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
    val orderProperty: String?,
    val displayColumnOrder: String?,
    val eventStatus: String?,
    val eventDate: String?,
    val lastUpdatedDate: String?,
    val programStage: String?,
    val trackedEntityInstances: String?,
    val enrollmentIncidentDate: String?,
    val enrollmentCreatedDate: String?,
    val trackedEntityType: String?,
)
