import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "AnalyticsTeiAttribute",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnalyticsTeiSettingDB::class,
            parentColumns = ["uid"],
            childColumns = ["teiSetting"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["attribute"]),
        Index(value = ["teiSetting"])
    ]
)
internal data class AnalyticsTeiAttributeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val teiSetting: String,
    val whoComponent: String?,
    val attribute: String
)
