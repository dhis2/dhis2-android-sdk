import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "RelationshipType",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class RelationshipTypeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val fromToName: String?,
    val toFromName: String?,
    val bidirectional: Int?,
    val accessDataWrite: Int?
)
