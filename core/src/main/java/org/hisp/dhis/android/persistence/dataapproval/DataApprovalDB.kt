import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataApproval",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["attributeOptionCombo"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PeriodDB::class,
            parentColumns = ["periodId"],
            childColumns = ["period"],
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
        Index(value = ["attributeOptionCombo", "period", "organisationUnit", "workflow"], unique = true),
        Index(value = ["attributeOptionCombo"]),
        Index(value = ["period"]),
        Index(value = ["organisationUnit"])
    ]
)
internal data class DataApprovalDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val workflow: String,
    val organisationUnit: String,
    val period: String,
    val attributeOptionCombo: String,
    val state: String?
)
