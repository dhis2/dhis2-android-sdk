import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataSetSetting",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class DataSetSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String?,
    val name: String?,
    val lastUpdated: String?,
    val periodDSDownload: Int?,
    val periodDSDBTrimming: Int?
)
