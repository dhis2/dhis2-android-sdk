import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetOrganisationUnitLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["organisationUnit", "dataSet"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["organisationUnit"])
    ]
)
internal data class DataSetOrganisationUnitLinkDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataSet: String,
    val organisationUnit: String
)
