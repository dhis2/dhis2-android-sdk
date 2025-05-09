package org.hisp.dhis.android.persistence.programstageworkinglist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.program.ProgramDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

@Entity(
    tableName = "ProgramStageWorkingList",
    foreignKeys = [
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
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["orgUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["orgUnit"]),
    ],
)
internal data class ProgramStageWorkingListDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val description: String?,
    val program: String,
    val programStage: String,
    val eventStatus: String?,
    val eventCreatedAt: String?,
    val eventOccurredAt: String?,
    val eventScheduledAt: String?,
    val enrollmentStatus: String?,
    val enrolledAt: String?,
    val enrollmentOccurredAt: String?,
    val orderProperty: String?,
    val displayColumnOrder: String?,
    val orgUnit: String?,
    val ouMode: String?,
    val assignedUserMode: String?,
)
