import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramSectionAttributeLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programSection"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["programSection", "attribute"], unique = true),
        Index(value = ["programSection"]),
        Index(value = ["attribute"])
    ]
)
internal data class ProgramSectionAttributeLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val programSection: String,
    val attribute: String,
    val sortOrder: Int?
)
