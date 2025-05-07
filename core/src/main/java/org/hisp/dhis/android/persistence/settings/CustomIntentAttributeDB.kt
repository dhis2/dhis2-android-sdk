import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CustomIntentAttribute",
    foreignKeys = [
        ForeignKey(
            entity = CustomIntentDB::class,
            parentColumns = ["uid"],
            childColumns = ["customIntentUid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["customIntentUid"])
    ]
)
internal data class CustomIntentAttributeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val customIntentUid: String
)
