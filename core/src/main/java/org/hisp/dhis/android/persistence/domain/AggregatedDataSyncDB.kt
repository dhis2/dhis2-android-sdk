import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "AggregatedDataSync",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dataSet"], unique = true)
    ]
)
internal data class AggregatedDataSyncDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataSet: String,
    val periodType: String,
    val pastPeriods: Int,
    val futurePeriods: Int,
    val dataElementsHash: Int,
    val organisationUnitsHash: Int,
    val lastUpdated: String
)
