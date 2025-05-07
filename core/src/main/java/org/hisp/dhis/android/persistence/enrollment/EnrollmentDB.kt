import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Enrollment",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"])
    ]
)
internal data class EnrollmentDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val created: String?,
    val lastUpdated: String?,
    val createdAtClient: String?,
    val lastUpdatedAtClient: String?,
    val organisationUnit: String,
    val program: String,
    val enrollmentDate: String?,
    val incidentDate: String?,
    val followup: Int?,
    val status: String?,
    val trackedEntityInstance: String,
    val syncState: String?,
    val aggregatedSyncState: String?,
    val geometryType: String?,
    val geometryCoordinates: String?,
    val deleted: Int?,
    val completedDate: String?
)
