// CREATE TABLE ProgramStageAttributeValueLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, programStage TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, FOREIGN KEY (programStage) REFERENCES ProgramStage (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attribute) REFERENCES Attribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (programStage, attribute));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramStageAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStage::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
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
        Index(value = ["programStage", "attribute"], unique = true),
        Index(value = ["programStage"]),
        Index(value = ["attribute"])
    ]
)
internal data class ProgramStageAttributeValueLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val programStage: String,
    val attribute: String,
    val value: String?
)
