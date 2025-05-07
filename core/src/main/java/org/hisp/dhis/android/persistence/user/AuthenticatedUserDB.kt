import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "AuthenticatedUser",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["uid"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user"], unique = true)
    ]
)
internal data class AuthenticatedUserDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val user: String,
    val hash: String?
)
