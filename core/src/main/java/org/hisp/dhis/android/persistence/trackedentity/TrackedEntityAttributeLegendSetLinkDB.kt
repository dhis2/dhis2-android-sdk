import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityAttributeLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LegendSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["legendSet"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trackedEntityAttribute", "legendSet"], unique = true),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["legendSet"])
    ]
)
internal data class TrackedEntityAttributeLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val trackedEntityAttribute: String,
    val legendSet: String,
    val sortOrder: Int?
)
