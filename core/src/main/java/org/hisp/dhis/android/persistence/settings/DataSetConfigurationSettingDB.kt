package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DataSetConfigurationSetting")
internal data class DataSetConfigurationSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String?,
    val minimumLocationAccuracy: Int?,
    val disableManualLocation: Int?,
)
