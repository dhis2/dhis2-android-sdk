import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
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
        Index(value = ["dataSet", "dataElement"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["dataElement"])
    ]
)
internal data class DataSetDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val dataSet: String,
    val dataElement: String,
    val categoryCombo: String?
)
