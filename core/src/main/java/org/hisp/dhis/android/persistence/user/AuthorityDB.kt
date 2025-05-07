import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Authority")
internal data class AuthorityDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val name: String?
)
