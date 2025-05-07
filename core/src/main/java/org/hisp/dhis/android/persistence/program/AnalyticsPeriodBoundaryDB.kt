import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "AnalyticsPeriodBoundary",
    foreignKeys = [
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["programIndicator"])
    ]
)
internal data class AnalyticsPeriodBoundaryDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val analyticsPeriodBoundaryType: String?,
    val boundaryTarget: String?,
    val programIndicator: String,
    val eventDate: String?,
    val enrollmentDate: String?,
    val incidentDate: String?,
    val created: String?,
    val lastUpdated: String?
)
