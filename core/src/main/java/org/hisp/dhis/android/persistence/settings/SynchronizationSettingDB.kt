import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SynchronizationSetting")
internal data class SynchronizationSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val dataSync: String?,
    val metadataSync: String?,
    val trackerImporterVersion: String?,
    val trackerExporterVersion: String?,
    val fileMaxLengthBytes: Int?
)
