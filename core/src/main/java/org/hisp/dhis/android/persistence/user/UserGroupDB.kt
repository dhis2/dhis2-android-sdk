package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.Authority
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate

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
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
) {
    fun toDomain(): UserGroup {
        return UserGroup.builder()
            .uid(uid)
            .code(code)
            .name(name)
            .displayName(displayName)
            .created(created.toJavaDate())
            .lastUpdated(lastUpdated.toJavaDate())
            .build()
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


