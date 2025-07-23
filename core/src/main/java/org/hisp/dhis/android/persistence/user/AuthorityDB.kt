package org.hisp.dhis.android.persistence.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.Authority
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "Authority")
internal data class AuthorityDB(
    @PrimaryKey
    val name: String?,
) : EntityDB<Authority> {

    override fun toDomain(): Authority {
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
