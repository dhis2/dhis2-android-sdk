package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.GeneralSettings
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(tableName = "GeneralSetting")
internal data class GeneralSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val encryptDB: Boolean?,
    val lastUpdated: String?,
    val reservedValues: Int?,
    val smsGateway: String?,
    val smsResultSender: String?,
    val matomoID: Int?,
    val matomoURL: String?,
    val allowScreenCapture: Boolean?,
    val messageOfTheDay: String?,
    val experimentalFeatures: StringListDB?,
    val bypassDHIS2VersionCheck: Boolean?,
) : EntityDB<GeneralSettings> {

    override fun toDomain(): GeneralSettings {
        return GeneralSettings.builder()
            .id(id?.toLong())
            .encryptDB(encryptDB)
            .lastUpdated(lastUpdated.toJavaDate())
            .reservedValues(reservedValues)
            .smsGateway(smsGateway)
            .smsResultSender(smsResultSender)
            .matomoID(matomoID)
            .matomoURL(matomoURL)
            .allowScreenCapture(allowScreenCapture)
            .messageOfTheDay(messageOfTheDay)
            .experimentalFeatures(experimentalFeatures?.toDomain())
            .bypassDHIS2VersionCheck(bypassDHIS2VersionCheck)
            .build()
    }
}

internal fun GeneralSettings.toDB(): GeneralSettingDB {
    return GeneralSettingDB(
        encryptDB = this.encryptDB(),
        lastUpdated = this.lastUpdated().dateFormat(),
        reservedValues = this.reservedValues(),
        smsGateway = this.smsGateway(),
        smsResultSender = this.smsResultSender(),
        matomoID = this.matomoID(),
        matomoURL = this.matomoURL(),
        allowScreenCapture = this.allowScreenCapture(),
        messageOfTheDay = this.messageOfTheDay(),
        experimentalFeatures = this.experimentalFeatures()?.toDB(),
        bypassDHIS2VersionCheck = this.bypassDHIS2VersionCheck(),
    )
}