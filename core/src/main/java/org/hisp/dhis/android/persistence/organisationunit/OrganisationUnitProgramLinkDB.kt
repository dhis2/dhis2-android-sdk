import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrganisationUnitProgramLink",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["organisationUnit", "program"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["program"])
    ]
)
internal data class OrganisationUnitProgramLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val organisationUnit: String,
    val program: String
)
