import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramTempOwner",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"])
    ]
)
internal data class ProgramTempOwnerDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val program: String,
    val trackedEntityInstance: String,
    val created: String,
    val validUntil: String,
    val reason: String
)
