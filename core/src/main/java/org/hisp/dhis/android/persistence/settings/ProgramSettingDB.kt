import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProgramSetting",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true)
    ]
)
internal data class ProgramSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String?,
    val name: String?,
    val lastUpdated: String?,
    val teiDownload: Int?,
    val teiDBTrimming: Int?,
    val eventsDownload: Int?,
    val eventsDBTrimming: Int?,
    val updateDownload: String?,
    val updateDBTrimming: String?,
    val settingDownload: String?,
    val settingDBTrimming: String?,
    val enrollmentDownload: String?,
    val enrollmentDBTrimming: String?,
    val eventDateDownload: String?,
    val eventDateDBTrimming: String?,
    val enrollmentDateDownload: String?,
    val enrollmentDateDBTrimming: String?
)
