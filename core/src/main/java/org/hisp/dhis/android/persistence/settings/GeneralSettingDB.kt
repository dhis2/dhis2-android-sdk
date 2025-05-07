import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GeneralSetting")
internal data class GeneralSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val encryptDB: Int?,
    val lastUpdated: String?,
    val reservedValues: Int?,
    val smsGateway: String?,
    val smsResultSender: String?,
    val matomoID: Int?,
    val matomoURL: String?,
    val allowScreenCapture: Int?,
    val messageOfTheDay: String?,
    val experimentalFeatures: String?,
    val bypassDHIS2VersionCheck: Int?
)
