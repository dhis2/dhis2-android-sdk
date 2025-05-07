import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SMSOngoingSubmission")
internal data class SMSOngoingSubmissionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val submissionId: Int?,
    val type: String?,
)
