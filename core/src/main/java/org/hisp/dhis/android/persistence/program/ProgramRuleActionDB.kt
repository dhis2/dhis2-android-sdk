import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramRuleAction",
    foreignKeys = [
        ForeignKey(
            entity = ProgramRuleDB::class,
            parentColumns = ["uid"],
            childColumns = ["programRule"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityAttribute"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = OptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["option"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = OptionGroupDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionGroup"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["programRule"]),
        Index(value = ["trackedEntityAttribute"]),
        Index(value = ["programIndicator"]),
        Index(value = ["programStageSection"]),
        Index(value = ["programStage"]),
        Index(value = ["dataElement"]),
        Index(value = ["option"]),
        Index(value = ["optionGroup"]),
    ],
)
internal data class ProgramRuleActionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val data: String?,
    val content: String?,
    val location: String?,
    val trackedEntityAttribute: String?,
    val programIndicator: String?,
    val programStageSection: String?,
    val programRuleActionType: String?,
    val programStage: String?,
    val dataElement: String?,
    val programRule: String,
    val option: String?,
    val optionGroup: String?,
    val displayContent: String?,
)
