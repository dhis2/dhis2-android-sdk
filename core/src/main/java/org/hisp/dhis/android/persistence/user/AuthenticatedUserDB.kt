package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val id: Int = 0,
    val user: String,
    val hash: String?,
)
