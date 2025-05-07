import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DataSetConfigurationSetting")
internal data class DataSetConfigurationSettingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String?,
    val minimumLocationAccuracy: Int?,
    val disableManualLocation: Int?
)
