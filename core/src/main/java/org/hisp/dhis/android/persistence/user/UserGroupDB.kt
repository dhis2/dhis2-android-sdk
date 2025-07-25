package org.hisp.dhis.android.persistence.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "UserGroup")
internal data class UserGroupDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
) : EntityDB<UserGroup>, BaseIdentifiableObjectDB {

    override fun toDomain(): UserGroup {
        return UserGroup.builder().apply {
            applyBaseIdentifiableFields(this@UserGroupDB)
        }.build()
    }
}

internal fun UserGroup.toDB(): UserGroupDB {
    return UserGroupDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        deleted = deleted(),
    )
}
