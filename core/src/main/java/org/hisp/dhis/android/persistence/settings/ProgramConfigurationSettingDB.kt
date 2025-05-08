package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ProgramConfigurationSetting")
internal data class ProgramConfigurationSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String?,
    val completionSpinner: Int?,
    val optionalSearch: Int?,
    val disableReferrals: Int?,
    val disableCollapsibleSections: Int?,
    val itemHeaderProgramIndicator: String?,
    val minimumLocationAccuracy: Int?,
    val disableManualLocation: Int?,
    val quickActions: String?,
)
