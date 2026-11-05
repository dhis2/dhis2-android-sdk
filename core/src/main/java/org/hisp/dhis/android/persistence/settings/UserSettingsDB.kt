package org.hisp.dhis.android.persistence.settings

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import org.hisp.dhis.android.core.settings.UserSettings
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "UserSettings")
internal data class UserSettingsDB(
    @PrimaryKey
    val keyUiLocale: String,
    val keyDbLocale: String?,
) : EntityDB<UserSettings> {

    override fun toDomain(): UserSettings {
        return UserSettings.builder()
            .keyUiLocale(keyUiLocale)
            .keyDbLocale(keyDbLocale)
            .build()
    }
}

internal fun UserSettings.toDB(): UserSettingsDB {
    return UserSettingsDB(
        keyUiLocale = keyUiLocale()!!,
        keyDbLocale = keyDbLocale(),
    )
}
