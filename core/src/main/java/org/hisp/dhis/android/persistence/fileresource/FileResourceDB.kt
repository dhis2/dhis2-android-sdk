package org.hisp.dhis.android.persistence.fileresource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDomain
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.itemfilter.toDB

@Entity(
    tableName = "FileResource",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class FileResourceDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val contentType: String?,
    val contentLength: Long?,
    val path: String?,
    override val syncState: SyncStateDB?,
    val domain: String?,
) : EntityDB<FileResource>, DataObjectDB {

    override fun toDomain(): FileResource {
        return FileResource.builder().apply {
            id(id?.toLong())
            uid(uid)
            name?.let { name(it) }
            created?.let { created(it.toJavaDate()!!) }
            lastUpdated?.let { lastUpdated(it.toJavaDate()!!) }
            contentType?.let { contentType(it) }
            contentLength?.let { contentLength(it) }
            path?.let { path(it) }
            syncState?.let { syncState(it.toDomain()) }
            domain?.let { domain(FileResourceDomain.valueOf(it)) }
        }.build()
    }
}

internal fun FileResource.toDB(): FileResourceDB {
    return FileResourceDB(
        uid = uid()!!,
        name = name(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        contentType = contentType(),
        contentLength = contentLength(),
        path = path(),
        syncState = syncState()?.toDB(),
        domain = domain()?.name,
    )
}
