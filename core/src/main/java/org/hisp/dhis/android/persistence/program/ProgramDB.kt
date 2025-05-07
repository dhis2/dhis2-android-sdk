import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Program",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["trackedEntityType"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["trackedEntityType"]),
        Index(value = ["categoryCombo"])
    ]
)
internal data class ProgramDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
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
    val version: Int?,
    val onlyEnrollOnce: Int?,
    val displayEnrollmentDateLabel: String?,
    val displayIncidentDate: Int?,
    val displayIncidentDateLabel: String?,
    val registration: Int?,
    val selectEnrollmentDatesInFuture: Int?,
    val dataEntryMethod: Int?,
    val ignoreOverdueEvents: Int?,
    val selectIncidentDatesInFuture: Int?,
    val useFirstStageDuringRegistration: Int?,
    val displayFrontPageList: Int?,
    val programType: String?,
    val relatedProgram: String?,
    val trackedEntityType: String?,
    val categoryCombo: String?,
    val accessDataWrite: Int?,
    val expiryDays: Int?,
    val completeEventsExpiryDays: Int?,
    val expiryPeriodType: String?,
    val minAttributesRequiredToSearch: Int?,
    val maxTeiCountToReturn: Int?,
    val featureType: String?,
    val accessLevel: String?,
    val color: String?,
    val icon: String?,
    val displayEnrollmentLabel: String?,
    val displayFollowUpLabel: String?,
    val displayOrgUnitLabel: String?,
    val displayRelationshipLabel: String?,
    val displayNoteLabel: String?,
    val displayTrackedEntityAttributeLabel: String?,
    val displayProgramStageLabel: String?,
    val displayEventLabel: String?
)
