package org.hisp.dhis.android.persistence.systeminfo

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SystemInfo")
internal data class SystemInfoDB(
    val serverDate: String?,
    val dateFormat: String?,
    val version: String?,
    @PrimaryKey
    val contextPath: String,
    val systemName: String?,
    val serverTimeZoneId: String?,
) : EntityDB<SystemInfo> {

    override fun toDomain(): SystemInfo {
        return SystemInfo.builder()
            .serverDate(serverDate.toJavaDate())
            .dateFormat(dateFormat)
            .version(version)
            .contextPath(contextPath)
            .systemName(systemName)
            .serverTimeZoneId(serverTimeZoneId)
            .build()
    }
}

internal fun SystemInfo.toDB(): SystemInfoDB {
    return SystemInfoDB(
        serverDate = serverDate().dateFormat(),
        dateFormat = dateFormat(),
        version = version(),
        contextPath = contextPath()!!,
        systemName = systemName(),
        serverTimeZoneId = serverTimeZoneId(),
    )
}
