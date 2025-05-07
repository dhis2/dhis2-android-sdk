import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SectionIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
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
        Index(value = ["section", "indicator"], unique = true),
        Index(value = ["section"]),
        Index(value = ["indicator"])
    ]
)
internal data class SectionIndicatorLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val section: String,
    val indicator: String
)
