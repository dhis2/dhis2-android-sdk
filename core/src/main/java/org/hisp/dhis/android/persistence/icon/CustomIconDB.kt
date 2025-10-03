package org.hisp.dhis.android.persistence.icon

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.icon.CustomIcon
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "CustomIcon")
internal data class CustomIconDB(
    @PrimaryKey
    val key: String,
    val fileResource: String,
    val href: String,
) : EntityDB<CustomIcon> {

    override fun toDomain(): CustomIcon {
        return CustomIcon.builder()
            .key(key)
            .fileResource(ObjectWithUid.create(fileResource))
            .href(href)
            .build()
    }
}

internal fun CustomIcon.toDB(): CustomIconDB {
    return CustomIconDB(
        key = key(),
        fileResource = fileResource().uid(),
        href = href(),
    )
}
