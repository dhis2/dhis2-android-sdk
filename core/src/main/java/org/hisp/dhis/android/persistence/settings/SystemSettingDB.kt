package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.SystemSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "SystemSetting",
    indices = [
        Index(value = ["key"], unique = true),
    ],
)
internal data class SystemSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
