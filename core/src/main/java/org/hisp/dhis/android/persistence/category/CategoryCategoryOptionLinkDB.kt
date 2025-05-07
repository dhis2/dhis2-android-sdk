

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDB::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryOptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category", "categoryOption"], unique = true),
        Index(value = ["category"]),
        Index(value = ["categoryOption"])
    ]
)
internal data class CategoryCategoryOptionLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val category: String,
    val categoryOption: String,
    val sortOrder: Int?
)
