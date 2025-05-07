import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataElementOperand",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["dataElement"]),
        Index(value = ["categoryOptionCombo"])
    ]
)
internal data class DataElementOperandDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val dataElement: String?,
    val categoryOptionCombo: String?
)
