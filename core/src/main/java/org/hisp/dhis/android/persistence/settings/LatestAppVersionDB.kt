package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.LatestAppVersion
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "LatestAppVersion")
internal data class LatestAppVersionDB(
    @PrimaryKey
    val version: String,
    val downloadURL: String?,
) : EntityDB<LatestAppVersion> {

    override fun toDomain(): LatestAppVersion {
        return LatestAppVersion.builder()
            .downloadURL(downloadURL)
            .version(version)
            .build()
    }
}

internal fun LatestAppVersion.toDB(): LatestAppVersionDB {
    return LatestAppVersionDB(
        downloadURL = downloadURL(),
        version = version()!!,
    )
}
