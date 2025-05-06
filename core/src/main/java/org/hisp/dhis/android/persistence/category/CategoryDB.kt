// CREATE TABLE Category (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, dataDimensionType TEXT);

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Category",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class Category(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val dataDimensionType: String?
)
