import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrganisationUnitLevel",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class OrganisationUnitLevelDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val level: Int?
)
