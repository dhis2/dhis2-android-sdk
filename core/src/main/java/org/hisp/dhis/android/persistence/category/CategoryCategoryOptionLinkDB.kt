// CREATE TABLE CategoryCategoryOptionLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT NOT NULL, categoryOption TEXT NOT NULL, sortOrder INTEGER, FOREIGN KEY (category) REFERENCES Category (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOption) REFERENCES CategoryOption (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (category, categoryOption));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryOption::class,
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
internal data class CategoryCategoryOptionLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val category: String,
    val categoryOption: String,
    val sortOrder: Int?
)
