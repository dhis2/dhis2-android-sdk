package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.AuthenticatedUser

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
    indices = [
        Index(value = ["user"], unique = true),
    ],
)
internal data class AuthenticatedUserDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val user: String,
    val hash: String?,
) {
    fun toDomain(): AuthenticatedUser {
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
