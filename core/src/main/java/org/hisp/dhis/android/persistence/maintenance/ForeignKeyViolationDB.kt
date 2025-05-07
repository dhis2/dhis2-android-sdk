import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ForeignKeyViolation")
internal data class ForeignKeyViolationDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val fromTable: String?,
    val fromColumn: String?,
    val toTable: String?,
    val toColumn: String?,
    val notFoundValue: String?,
    val fromObjectUid: String?,
    val fromObjectRow: String?,
    val created: String?
)
