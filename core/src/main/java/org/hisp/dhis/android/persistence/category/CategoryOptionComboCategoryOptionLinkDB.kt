// CREATE TABLE CategoryOptionComboCategoryOptionLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, categoryOptionCombo TEXT NOT NULL, categoryOption TEXT NOT NULL, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOption) REFERENCES CategoryOption (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (categoryOptionCombo, categoryOption));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOptionComboCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionCombo::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
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
        Index(value = ["categoryOptionCombo", "categoryOption"], unique = true),
        Index(value = ["categoryOptionCombo"]),
        Index(value = ["categoryOption"])
    ]
)
internal data class CategoryOptionComboCategoryOptionLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val categoryOptionCombo: String,
    val categoryOption: String
)
