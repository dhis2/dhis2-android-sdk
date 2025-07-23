package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SystemSetting")
internal data class SystemSettingDB(
    @PrimaryKey
    val key: String?,
    val value: String?,
) : EntityDB<SystemSetting> {

    override fun toDomain(): SystemSetting {
        return SystemSetting.builder().apply {
            key?.let { key(SystemSetting.SystemSettingKey.valueOf(it)) }
            value(value)
        }.build()
    }
}

internal fun SystemSetting.toDB(): SystemSettingDB {
    return SystemSettingDB(
        key = key()?.name,
        value = value(),
    )
}
