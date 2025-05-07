import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SectionGreyedFieldsLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DataElementOperandDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElementOperand"],
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
        Index(value = ["section", "dataElementOperand", "categoryOptionCombo"], unique = true),
        Index(value = ["section"]),
        Index(value = ["dataElementOperand"]),
        Index(value = ["categoryOptionCombo"])
    ]
)
internal data class SectionGreyedFieldsLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val section: String,
    val dataElementOperand: String,
    val categoryOptionCombo: String?
)
