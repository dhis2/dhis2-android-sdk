import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityInstanceFilter",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"])
    ]
)
internal data class TrackedEntityInstanceFilterDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
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
    val trackedEntityType: String?
)
