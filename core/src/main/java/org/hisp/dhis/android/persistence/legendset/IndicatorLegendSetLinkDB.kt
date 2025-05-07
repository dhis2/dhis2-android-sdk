import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "IndicatorLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
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
        Index(value = ["indicator", "legendSet"], unique = true),
        Index(value = ["indicator"]),
        Index(value = ["legendSet"])
    ]
)
internal data class IndicatorLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val indicator: String,
    val legendSet: String,
    val sortOrder: Int?
)
