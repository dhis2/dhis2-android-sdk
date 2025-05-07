import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityInstanceSync",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["program", "organisationUnitIdsHash"], unique = true)
    ]
)
internal data class TrackedEntityInstanceSyncDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val program: String?,
    val organisationUnitIdsHash: Int?,
    val downloadLimit: Int,
    val lastUpdated: String
)
