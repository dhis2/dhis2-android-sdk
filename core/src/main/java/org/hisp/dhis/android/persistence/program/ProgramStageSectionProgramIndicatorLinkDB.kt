import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStageSectionProgramIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["programStageSection", "programIndicator"], unique = true),
        Index(value = ["programStageSection"]),
        Index(value = ["programIndicator"])
    ]
)
internal data class ProgramStageSectionProgramIndicatorLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val programStageSection: String,
    val programIndicator: String
)
