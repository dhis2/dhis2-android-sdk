package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.UserRole
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate

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
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
) {
    fun toDomain(): UserRole {
        return UserRole.builder()
            .uid(uid)
            .code(code)
            .name(name)
            .displayName(displayName)
            .created(created.toJavaDate())
            .lastUpdated(lastUpdated.toJavaDate())
            .build()
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
