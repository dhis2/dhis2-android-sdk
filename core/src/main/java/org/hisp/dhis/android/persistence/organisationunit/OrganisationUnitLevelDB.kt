package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "OrganisationUnitLevel",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class OrganisationUnitLevelDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val level: Int?,
) : EntityDB<OrganisationUnitLevel>, BaseIdentifiableObjectDB {

    override fun toDomain(): OrganisationUnitLevel {
        return OrganisationUnitLevel.builder()
            .applyBaseIdentifiableFields(this)
            .id(id?.toLong())
            .level(level)
            .build()
    }
}

internal fun OrganisationUnitLevel.toDB(): OrganisationUnitLevelDB {
    return OrganisationUnitLevelDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        level = level(),
    )
}
