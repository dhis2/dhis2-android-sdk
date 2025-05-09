package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.core.user.Authority

@Entity(tableName = "Authority")
internal data class AuthorityDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val name: String?,
) {
    fun toDomain(): Authority {
        return Authority.builder()
            .name(name)
            .build()
    }
}

internal fun Authority.toDB(): AuthorityDB {
    return AuthorityDB(
        name = name(),
    )
}

