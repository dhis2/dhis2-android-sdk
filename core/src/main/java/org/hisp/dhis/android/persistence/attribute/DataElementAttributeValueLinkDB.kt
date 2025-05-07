

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataElementAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
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
        Index(value = ["dataElement", "attribute"], unique = true),
        Index(value = ["dataElement"]),
        Index(value = ["attribute"])
    ]
)
internal data class DataElementAttributeValueLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataElement: String,
    val attribute: String,
    val value: String?
)
