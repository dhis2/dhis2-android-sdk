// CREATE TABLE CategoryOptionCombo (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, categoryCombo TEXT, FOREIGN KEY (categoryCombo) REFERENCES CategoryCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOptionCombo",
    foreignKeys = [
        ForeignKey(
            entity = CategoryCombo::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["categoryCombo"])
    ]
)
internal data class CategoryOptionCombo(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val categoryCombo: String?
)
