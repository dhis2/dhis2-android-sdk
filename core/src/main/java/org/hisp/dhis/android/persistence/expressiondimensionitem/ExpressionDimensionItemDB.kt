import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ExpressionDimensionItem",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class ExpressionDimensionItemDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val expression: String?
)
