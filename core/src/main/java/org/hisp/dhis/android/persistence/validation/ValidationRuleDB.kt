import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ValidationRule",
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class ValidationRuleDB(
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
    val instruction: String?,
    val importance: String?,
    val operator: String?,
    val periodType: String?,
    val skipFormValidation: Int?,
    val leftSideExpression: String?,
    val leftSideDescription: String?,
    val leftSideMissingValueStrategy: String?,
    val rightSideExpression: String?,
    val rightSideDescription: String?,
    val rightSideMissingValueStrategy: String?,
    val organisationUnitLevels: String?
)
