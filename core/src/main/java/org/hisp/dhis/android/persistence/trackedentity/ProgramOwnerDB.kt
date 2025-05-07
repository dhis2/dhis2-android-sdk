import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramOwner",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackedEntityInstanceDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityInstance"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["ownerOrgUnit"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["program", "trackedEntityInstance"], unique = true),
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"]),
        Index(value = ["ownerOrgUnit"])
    ]
)
internal data class ProgramOwnerDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val program: String,
    val trackedEntityInstance: String,
    val ownerOrgUnit: String,
    val syncState: String?
)
