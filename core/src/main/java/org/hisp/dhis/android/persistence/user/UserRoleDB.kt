package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.UserRole
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "UserRole",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class UserRoleDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
) : EntityDB<UserRole>, BaseIdentifiableObjectDB {

    override fun toDomain(): UserRole {
        return UserRole.builder().apply {
            applyBaseIdentifiableFields(this@UserRoleDB)
        }.build()
    }
}

internal fun UserRole.toDB(): UserRoleDB {
    return UserRoleDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
    )
}
