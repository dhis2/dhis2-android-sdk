package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataSync: String?,
    val metadataSync: String?,
    val trackerImporterVersion: String?,
    val trackerExporterVersion: String?,
    val fileMaxLengthBytes: Int?,
) : EntityDB<SynchronizationSettings> {

    override fun toDomain(): SynchronizationSettings {
        return SynchronizationSettings.builder()
            .id(id?.toLong())
            .dataSync(dataSync?.let { DataSyncPeriod.valueOf(it) })
            .metadataSync(metadataSync?.let { MetadataSyncPeriod.valueOf(it) })
            .trackerImporterVersion(trackerImporterVersion?.let { TrackerImporterVersion.valueOf(it) })
            .trackerExporterVersion(trackerExporterVersion?.let { TrackerExporterVersion.valueOf(it) })
            .fileMaxLengthBytes(fileMaxLengthBytes)
            .build()
    }
}

internal fun SynchronizationSettings.toDB(): SynchronizationSettingDB {
    return SynchronizationSettingDB(
        dataSync = dataSync()?.name,
        metadataSync = metadataSync()?.name,
        trackerImporterVersion = trackerImporterVersion()?.name,
        trackerExporterVersion = trackerExporterVersion()?.name,
        fileMaxLengthBytes = fileMaxLengthBytes(),
    )
}
