import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataElementLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
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
        Index(value = ["dataElement", "legendSet"], unique = true),
        Index(value = ["dataElement"]),
        Index(value = ["legendSet"])
    ]
)
internal data class DataElementLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataElement: String,
    val legendSet: String,
    val sortOrder: Int?
)
