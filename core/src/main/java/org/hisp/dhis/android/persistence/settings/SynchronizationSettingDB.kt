package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.DataSyncPeriod
import org.hisp.dhis.android.core.settings.MetadataSyncPeriod
import org.hisp.dhis.android.core.settings.SynchronizationSettings
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SynchronizationSetting")
internal data class SynchronizationSettingDB(
    @PrimaryKey
    val dataSync: String,
    val metadataSync: String?,
    val trackerImporterVersion: String?,
    val trackerExporterVersion: String?,
    val fileMaxLengthBytes: Int?,
) : EntityDB<SynchronizationSettings> {

    override fun toDomain(): SynchronizationSettings {
        return SynchronizationSettings.builder().apply {
            dataSync?.let { dataSync(DataSyncPeriod.valueOf(it)) }
            metadataSync?.let { metadataSync(MetadataSyncPeriod.valueOf(it)) }
            trackerImporterVersion?.let { trackerImporterVersion(TrackerImporterVersion.valueOf(it)) }
            trackerExporterVersion?.let { trackerExporterVersion(TrackerExporterVersion.valueOf(it)) }
            fileMaxLengthBytes(fileMaxLengthBytes)
        }.build()
    }
}

internal fun SynchronizationSettings.toDB(): SynchronizationSettingDB {
    return SynchronizationSettingDB(
        dataSync = dataSync()?.name!!,
        metadataSync = metadataSync()?.name,
        trackerImporterVersion = trackerImporterVersion()?.name,
        trackerExporterVersion = trackerExporterVersion()?.name,
        fileMaxLengthBytes = fileMaxLengthBytes(),
    )
}
