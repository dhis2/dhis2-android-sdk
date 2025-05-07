import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityDataValue",
    foreignKeys = [
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["event"]),
        Index(value = ["dataElement"])
    ]
)
internal data class TrackedEntityDataValueDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val event: String,
    val dataElement: String,
    val storedBy: String?,
    val value: String?,
    val created: String?,
    val lastUpdated: String?,
    val providedElsewhere: Int?,
    val syncState: String?
)
