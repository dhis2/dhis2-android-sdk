package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GeneralSetting")
internal data class GeneralSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
    val bypassDHIS2VersionCheck: Int?,
)
