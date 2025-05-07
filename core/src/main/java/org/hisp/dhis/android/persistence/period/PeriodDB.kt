import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Period",
    indices = [
        Index(value = ["periodId"], unique = true)
    ]
)
internal data class PeriodDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val periodId: String?,
    val periodType: String?,
    val startDate: String?,
    val endDate: String?
)
