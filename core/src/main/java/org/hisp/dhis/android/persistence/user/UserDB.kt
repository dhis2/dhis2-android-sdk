package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "User",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class UserDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val birthday: String?,
    val education: String?,
    val gender: String?,
    val jobTitle: String?,
    val surname: String?,
    val firstName: String?,
    val introduction: String?,
    val employer: String?,
    val interests: String?,
    val languages: String?,
    val email: String?,
    val phoneNumber: String?,
    val nationality: String?,
    val username: String?,
)
