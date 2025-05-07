import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "RelationshipConstraint",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
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
        Index(value = ["relationshipType", "constraintType"], unique = true),
        Index(value = ["trackedEntityType"]),
        Index(value = ["program"]),
        Index(value = ["programStage"])
    ]
)
internal data class RelationshipConstraintDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val relationshipType: String,
    val constraintType: String,
    val relationshipEntity: String?,
    val trackedEntityType: String?,
    val program: String?,
    val programStage: String?,
    val trackerDataViewAttributes: String?,
    val trackerDataViewDataElements: String?
)
