import androidx.room.ColumnInfo
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
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["dataSet"], unique = true),
    ],
)
internal data class AggregatedDataSyncDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val dataSet: String,
    val periodType: String,
    val pastPeriods: Int,
    val futurePeriods: Int,
    val dataElementsHash: Int,
    val organisationUnitsHash: Int,
    val lastUpdated: String,
)
