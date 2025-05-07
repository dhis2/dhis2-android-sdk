import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStageWorkingList",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["orgUnit"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
        Index(value = ["programStage"]),
        Index(value = ["orgUnit"])
    ]
)
internal data class ProgramStageWorkingListDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
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
    val assignedUserMode: String?
)
