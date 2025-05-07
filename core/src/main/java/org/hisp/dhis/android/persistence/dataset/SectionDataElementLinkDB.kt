import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SectionDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
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
        Index(value = ["section", "dataElement"], unique = true),
        Index(value = ["section"]),
        Index(value = ["dataElement"])
    ]
)
internal data class SectionDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val section: String,
    val dataElement: String,
    val sortOrder: Int?
)
