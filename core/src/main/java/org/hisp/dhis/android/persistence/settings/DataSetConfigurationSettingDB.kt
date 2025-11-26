package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.DataSetConfigurationSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "DataSetConfigurationSetting")
internal data class DataSetConfigurationSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String?,
    val minimumLocationAccuracy: Int?,
    val disableManualLocation: Boolean?,
) : EntityDB<DataSetConfigurationSetting> {

    override fun toDomain(): DataSetConfigurationSetting {
        return DataSetConfigurationSetting.builder()
            .uid(uid)
            .minimumLocationAccuracy(minimumLocationAccuracy)
            .disableManualLocation(disableManualLocation)
            .build()
    }
}

internal fun DataSetConfigurationSetting.toDB(): DataSetConfigurationSettingDB {
    return DataSetConfigurationSettingDB(
        uid = uid(),
        minimumLocationAccuracy = minimumLocationAccuracy(),
        disableManualLocation = disableManualLocation(),
    )
}
