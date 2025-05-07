import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SMSOngoingSubmission")
internal data class SMSOngoingSubmissionDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val submissionId: Int?,
    val type: String?
)
