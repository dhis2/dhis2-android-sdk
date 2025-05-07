import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityInstanceEventFilter",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityInstanceFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstanceFilter"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["trackedEntityInstanceFilter"]),
    ],
)
internal data class TrackedEntityInstanceEventFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val trackedEntityInstanceFilter: String,
    val programStage: String?,
    val eventStatus: String?,
    val periodFrom: Int?,
    val periodTo: Int?,
    val assignedUserMode: String?,
)
