import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dataSet", "indicator"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["indicator"])
    ]
)
internal data class DataSetIndicatorLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val dataSet: String,
    val indicator: String
)
