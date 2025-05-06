// CREATE TABLE CategoryCategoryComboLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT NOT NULL, categoryCombo TEXT NOT NULL, sortOrder INTEGER, FOREIGN KEY (category) REFERENCES Category (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryCombo) REFERENCES CategoryCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (category, categoryCombo));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryCategoryComboLink",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryCombo::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category", "categoryCombo"], unique = true),
        Index(value = ["category"]),
        Index(value = ["categoryCombo"])
    ]
)
internal data class CategoryCategoryComboLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val category: String,
    val categoryCombo: String,
    val sortOrder: Int?
)
