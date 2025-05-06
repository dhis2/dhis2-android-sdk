// CREATE TABLE DataElementAttributeValueLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataElement TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attribute) REFERENCES Attribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (dataElement, attribute));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataElementAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = DataElement::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Attribute::class,
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
internal data class DataElementAttributeValueLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val dataElement: String,
    val attribute: String,
    val value: String?
)
