import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserOrganisationUnit",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["uid"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["organisationUnitScope", "user", "organisationUnit"], unique = true),
        Index(value = ["user"])
    ]
)
internal data class UserOrganisationUnitDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val user: String,
    val organisationUnit: String,
    val organisationUnitScope: String,
    val root: Int?,
    val userAssigned: Int?
)
