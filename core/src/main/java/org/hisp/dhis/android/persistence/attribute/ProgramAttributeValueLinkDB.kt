// CREATE TABLE ProgramAttributeValueLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, program TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attribute) REFERENCES Attribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (program, attribute));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
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
        Index(value = ["program", "attribute"], unique = true),
        Index(value = ["program"]),
        Index(value = ["attribute"])
    ]
)
internal data class ProgramAttributeValueLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val program: String,
    val attribute: String,
    val value: String?
)
