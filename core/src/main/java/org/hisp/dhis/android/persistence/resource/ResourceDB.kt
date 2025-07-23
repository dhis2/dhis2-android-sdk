package org.hisp.dhis.android.persistence.resource

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.resource.internal.Resource
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "Resource")
internal data class ResourceDB(
    @PrimaryKey
    val resourceType: String,
    val lastSynced: String?,
) : EntityDB<Resource> {

    override fun toDomain(): Resource {
        return Resource.builder()
            .resourceType(resourceType.let { Resource.Type.valueOf(it) })
            .lastSynced(lastSynced.toJavaDate())
            .build()
    }
}

internal fun Resource.toDB(): ResourceDB {
    return ResourceDB(
        resourceType = resourceType()?.name!!,
        lastSynced = lastSynced().dateFormat(),
    )
}
