package org.hisp.dhis.android.persistence.systeminfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.systeminfo.SystemInfo
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SystemInfo")
internal data class SystemInfoDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val serverDate: String?,
    val dateFormat: String?,
    val version: String?,
    val contextPath: String?,
    val systemName: String?,
) : EntityDB<SystemInfo> {

    override fun toDomain(): SystemInfo {
        return SystemInfo.builder()
            .id(id?.toLong())
            .serverDate(serverDate.toJavaDate())
            .dateFormat(dateFormat)
            .version(version)
            .contextPath(contextPath)
            .systemName(systemName)
            .build()
    }

    companion object {
        fun SystemInfo.toDB(): SystemInfoDB {
            return SystemInfoDB(
                serverDate = serverDate().dateFormat(),
                dateFormat = dateFormat(),
                version = version(),
                contextPath = contextPath(),
                systemName = systemName()
            )
        }
    }
}
