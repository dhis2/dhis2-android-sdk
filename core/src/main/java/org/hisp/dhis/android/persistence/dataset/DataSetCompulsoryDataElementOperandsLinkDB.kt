import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetCompulsoryDataElementOperandsLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DataElementOperandDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElementOperand"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dataSet", "dataElementOperand"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["dataElementOperand"])
    ]
)
internal data class DataSetCompulsoryDataElementOperandsLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataSet: String,
    val dataElementOperand: String
)
