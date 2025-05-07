

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["program", "attribute"], unique = true),
        Index(value = ["program"]),
        Index(value = ["attribute"])
    ]
)
internal data class ProgramAttributeValueLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val program: String,
    val attribute: String,
    val value: String?
)
