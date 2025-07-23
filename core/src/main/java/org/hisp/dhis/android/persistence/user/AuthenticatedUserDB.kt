package org.hisp.dhis.android.persistence.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.AuthenticatedUser
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "AuthenticatedUser",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["uid"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class AuthenticatedUserDB(
    @PrimaryKey
    val user: String,
    val hash: String?,
) : EntityDB<AuthenticatedUser> {

    override fun toDomain(): AuthenticatedUser {
        return AuthenticatedUser.builder()
            .hash(hash)
            .user(user)
            .build()
    }
}

internal fun AuthenticatedUser.toDB(): AuthenticatedUserDB {
    return AuthenticatedUserDB(
        user = user()!!,
        hash = hash(),
    )
}
