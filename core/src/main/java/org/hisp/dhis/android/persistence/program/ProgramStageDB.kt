package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStage",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
    ],
)
internal data class ProgramStageDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val displayExecutionDateLabel: String?,
    val allowGenerateNextVisit: Int?,
    val validCompleteOnly: Int?,
    val reportDateToUse: String?,
    val openAfterEnrollment: Int?,
    val repeatable: Int?,
    val formType: String?,
    val displayGenerateEventBox: Int?,
    val generatedByEnrollmentDate: Int?,
    val autoGenerateEvent: Int?,
    val sortOrder: Int?,
    val hideDueDate: Int?,
    val blockEntryForm: Int?,
    val minDaysFromStart: Int?,
    val standardInterval: Int?,
    val program: String,
    val periodType: String?,
    val accessDataWrite: Int?,
    val remindCompleted: Int?,
    val description: String?,
    val displayDescription: String?,
    val featureType: String?,
    val color: String?,
    val icon: String?,
    val enableUserAssignment: Int?,
    val displayDueDateLabel: String?,
    val validationStrategy: String?,
    val displayProgramStageLabel: String?,
    val displayEventLabel: String?,
)
