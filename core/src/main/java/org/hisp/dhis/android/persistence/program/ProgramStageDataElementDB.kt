import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStageDataElement",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
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
        Index(value = ["uid"], unique = true),
        Index(value = ["programStage"]),
        Index(value = ["dataElement"])
    ]
)
internal data class ProgramStageDataElementDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val displayInReports: Int?,
    val compulsory: Int?,
    val allowProvidedElsewhere: Int?,
    val sortOrder: Int?,
    val allowFutureDate: Int?,
    val dataElement: String,
    val programStage: String
)
