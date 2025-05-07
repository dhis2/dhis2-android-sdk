import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackedEntityAttribute",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["optionSet"])
    ]
)
internal data class TrackedEntityAttributeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val pattern: String?,
    val sortOrderInListNoProgram: Int?,
    val optionSet: String?,
    val valueType: String?,
    val expression: String?,
    val programScope: Int?,
    val displayInListNoProgram: Int?,
    val generated: Int?,
    val displayOnVisitSchedule: Int?,
    val orgunitScope: Int?,
    val uniqueProperty: Int?,
    val inherit: Int?,
    val formName: String?,
    val fieldMask: String?,
    val color: String?,
    val icon: String?,
    val displayFormName: String?,
    val aggregationType: String?,
    val confidential: Int?
)
