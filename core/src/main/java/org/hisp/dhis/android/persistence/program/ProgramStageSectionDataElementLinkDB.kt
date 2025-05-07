import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStageSectionDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
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
        Index(value = ["programStageSection"]),
        Index(value = ["dataElement"])
    ]
)
internal data class ProgramStageSectionDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val programStageSection: String,
    val dataElement: String,
    val sortOrder: Int
)
