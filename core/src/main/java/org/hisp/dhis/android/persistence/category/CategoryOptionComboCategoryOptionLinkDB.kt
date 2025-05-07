

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOptionComboCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
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
        Index(value = ["categoryOptionCombo", "categoryOption"], unique = true),
        Index(value = ["categoryOptionCombo"]),
        Index(value = ["categoryOption"])
    ]
)
internal data class CategoryOptionComboCategoryOptionLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val categoryOptionCombo: String,
    val categoryOption: String
)
