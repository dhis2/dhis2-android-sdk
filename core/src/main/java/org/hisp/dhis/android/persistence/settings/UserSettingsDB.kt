package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.UserSettings
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "UserSettings")
internal data class UserSettingsDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val keyUiLocale: String?,
    val keyDbLocale: String?,
) : EntityDB<UserSettings> {

    override fun toDomain(): UserSettings {
        return UserSettings.builder()
            .id(id?.toLong())
            .keyUiLocale(keyUiLocale)
            .keyDbLocale(keyDbLocale)
            .build()
    }
}

internal fun UserSettings.toDB(): UserSettingsDB {
    return UserSettingsDB(
        keyUiLocale = keyUiLocale(),
        keyDbLocale = keyDbLocale(),
    )
}
