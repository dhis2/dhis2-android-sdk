import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Note",
    foreignKeys = [
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["noteType", "event", "enrollment", "value", "storedBy", "storedDate"], unique = true),
        Index(value = ["event"]),
        Index(value = ["enrollment"])
    ]
)
internal data class NoteDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val noteType: String?,
    val event: String?,
    val enrollment: String?,
    val value: String?,
    val storedBy: String?,
    val storedDate: String?,
    val uid: String?,
    val syncState: String?,
    val deleted: Int?
)
