import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "EventSync",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["program", "organisationUnitIdsHash"], unique = true),
        Index(value = ["program"])
    ]
)
internal data class EventSyncDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val program: String?,
    val organisationUnitIdsHash: Int?,
    val downloadLimit: Int,
    val lastUpdated: String
)
