import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrganisationUnit",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class OrganisationUnitDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val path: String?,
    val openingDate: String?,
    val closedDate: String?,
    val level: Int?,
    val parent: String?,
    val displayNamePath: String?,
    val geometryType: String?,
    val geometryCoordinates: String?
)
