import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackerVisualizationDimension",
    foreignKeys = [
        ForeignKey(
            entity = TrackerVisualizationDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackerVisualization"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trackerVisualization"]),
        Index(value = ["program"]),
        Index(value = ["programStage"])
    ]
)
internal data class TrackerVisualizationDimensionDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val trackerVisualization: String,
    val position: String,
    val dimension: String,
    val dimensionType: String?,
    val program: String?,
    val programStage: String?,
    val items: String?,
    val filter: String?,
    val repetition: String?
)
