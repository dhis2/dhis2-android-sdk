package org.hisp.dhis.android.persistence.tracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerJobObject
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(tableName = "TrackerJobObject")
internal data class TrackerJobObjectDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val trackerType: String,
    val objectUid: String,
    val jobUid: String,
    val lastUpdated: String,
    val fileResources: StringListDB?,
) : EntityDB<TrackerJobObject> {
    override fun toDomain(): TrackerJobObject {
        return TrackerJobObject.builder()
            .trackerType(trackerType.let { TrackerImporterObjectType.valueOf(it) })
            .objectUid(objectUid)
            .jobUid(jobUid)
            .lastUpdated(lastUpdated.toJavaDate())
            .fileResources(fileResources?.toDomain())
            .build()
    }
}

internal fun TrackerJobObject.toDB(): TrackerJobObjectDB {
    return TrackerJobObjectDB(
        trackerType = trackerType().name,
        objectUid = objectUid(),
        jobUid = jobUid(),
        lastUpdated = lastUpdated().dateFormat()!!,
        fileResources = fileResources().toDB(),
    )
}
