package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "UserGroup",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class UserGroupDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
) : EntityDB<UserGroup>, BaseIdentifiableObjectDB {

    override fun toDomain(): UserGroup {
        return UserGroup.builder().apply {
            applyBaseIdentifiableFields(this@UserGroupDB)
            id(id?.toLong())
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
    )
}
