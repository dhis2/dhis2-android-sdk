

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOption",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class CategoryOptionDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
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
