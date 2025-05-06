// CREATE TABLE CategoryOptionOrganisationUnitLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, categoryOption TEXT NOT NULL, organisationUnit TEXT, restriction TEXT, FOREIGN KEY (categoryOption) REFERENCES CategoryOption (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (categoryOption, organisationUnit));

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CategoryOptionOrganisationUnitLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOption::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrganisationUnit::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryOption", "organisationUnit"], unique = true),
        Index(value = ["categoryOption"]),
        Index(value = ["organisationUnit"])
    ]
)
internal data class CategoryOptionOrganisationUnitLink(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val categoryOption: String,
    val organisationUnit: String?,
    val restriction: String?
)
