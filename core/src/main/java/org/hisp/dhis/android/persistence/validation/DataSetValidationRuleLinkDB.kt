import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetValidationRuleLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ValidationRuleDB::class,
            parentColumns = ["uid"],
            childColumns = ["validationRule"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dataSet", "validationRule"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["validationRule"])
    ]
)
internal data class DataSetValidationRuleLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataSet: String,
    val validationRule: String
)
