import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "RelationshipType",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class RelationshipTypeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val fromToName: String?,
    val toFromName: String?,
    val bidirectional: Int?,
    val accessDataWrite: Int?,
)
