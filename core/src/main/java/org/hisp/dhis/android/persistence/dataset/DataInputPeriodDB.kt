import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataInputPeriod",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dataSet"])
    ]
)
internal data class DataInputPeriodDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val dataSet: String,
    val period: String,
    val openingDate: String?,
    val closingDate: String?
)
