package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.LatestAppVersion
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "LatestAppVersion")
internal data class LatestAppVersionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val downloadURL: String?,
    val version: String?,
) : EntityDB<LatestAppVersion> {

    override fun toDomain(): LatestAppVersion {
        return LatestAppVersion.builder()
            .id(id?.toLong())
            .downloadURL(downloadURL)
            .version(version)
            .build()
    }
}

internal fun LatestAppVersion.toDB(): LatestAppVersionDB {
    return LatestAppVersionDB(
        downloadURL = downloadURL(),
        version = version(),
    )
}
