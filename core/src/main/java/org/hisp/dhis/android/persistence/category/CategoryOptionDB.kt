// CREATE TABLE CategoryOption (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, startDate TEXT, endDate TEXT, accessDataWrite INTEGER);

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOption",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class CategoryOption(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val startDate: String?,
    val endDate: String?,
    val accessDataWrite: Int?
)
