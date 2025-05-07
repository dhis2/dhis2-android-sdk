import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ItemFilter",
    foreignKeys = [
        ForeignKey(
            entity = EventFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["eventFilter"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TrackedEntityInstanceFilterDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstanceFilter"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProgramStageWorkingListDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageWorkingList"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["eventFilter"]),
        Index(value = ["trackedEntityInstanceFilter"]),
        Index(value = ["programStageWorkingList"]),
    ],
)
internal data class ItemFilterDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val eventFilter: String?,
    val dataItem: String?,
    val trackedEntityInstanceFilter: String?,
    val attribute: String?,
    val programStageWorkingList: String?,
    val sw: String?,
    val ew: String?,
    val le: String?,
    val ge: String?,
    val gt: String?,
    val lt: String?,
    val eq: String?,
    val inProperty: String?,
    val like: String?,
    val dateFilter: String?,
)
