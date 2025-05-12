package org.hisp.dhis.android.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "UserOrganisationUnit",
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
        Index(value = ["organisationUnitScope", "user", "organisationUnit"], unique = true),
        Index(value = ["user"]),
    ],
)
internal data class UserOrganisationUnitDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val user: String,
    val organisationUnit: String,
    val organisationUnitScope: String,
    val root: Boolean?,
    val userAssigned: Boolean?,
) : EntityDB<UserOrganisationUnitLink> {

    override fun toDomain(): UserOrganisationUnitLink {
        return UserOrganisationUnitLink.builder()
            .id(id?.toLong())
            .user(user)
            .organisationUnit(organisationUnit)
            .organisationUnitScope(organisationUnitScope)
            .root(root)
            .userAssigned(userAssigned)
            .build()
    }
}

internal fun UserOrganisationUnitLink.toDB(): UserOrganisationUnitDB {
    return UserOrganisationUnitDB(
        user = user(),
        organisationUnit = organisationUnit(),
        organisationUnitScope = organisationUnitScope(),
        root = root(),
        userAssigned = userAssigned(),
    )
}
