import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackerImportConflict",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TrackerImportConflictDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val conflict: String?,
    val value: String?,
    val trackedEntityInstance: String?,
    val enrollment: String?,
    val event: String?,
    val tableReference: String?,
    val errorCode: String?,
    val status: String?,
    val created: String?,
    val displayDescription: String?,
    val trackedEntityAttribute: String?,
    val dataElement: String?
)
