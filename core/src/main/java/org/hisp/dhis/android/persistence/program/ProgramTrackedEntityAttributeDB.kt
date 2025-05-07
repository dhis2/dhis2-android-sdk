import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramTrackedEntityAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["program"])
    ]
)
internal data class ProgramTrackedEntityAttributeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val mandatory: Int?,
    val trackedEntityAttribute: String,
    val allowFutureDate: Int?,
    val displayInList: Int?,
    val program: String,
    val sortOrder: Int?,
    val searchable: Int?
)
