// CREATE TABLE CategoryCombo (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, isDefault INTEGER);

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryCombo",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class CategoryCombo(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val isDefault: Int?
)
