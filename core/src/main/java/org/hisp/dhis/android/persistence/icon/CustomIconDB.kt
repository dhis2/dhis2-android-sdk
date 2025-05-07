import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomIcon")
internal data class CustomIconDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val key: String,
    val fileResource: String,
    val href: String
)
