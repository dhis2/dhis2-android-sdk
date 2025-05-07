import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Relationship",
    foreignKeys = [
        ForeignKey(
            entity = RelationshipTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["relationshipType"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["relationshipType"])
    ]
)
internal data class RelationshipDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val relationshipType: String,
    val syncState: String?,
    val deleted: Int?
)
