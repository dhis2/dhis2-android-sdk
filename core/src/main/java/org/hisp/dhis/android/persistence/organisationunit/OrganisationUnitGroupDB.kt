package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "OrganisationUnitGroup")
internal data class OrganisationUnitGroupDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val shortName: String?,
    val displayShortName: String?,
) : EntityDB<OrganisationUnitGroup>, BaseIdentifiableObjectDB {

    override fun toDomain(): OrganisationUnitGroup {
        return OrganisationUnitGroup.builder()
            .applyBaseIdentifiableFields(this)
            .shortName(shortName)
            .displayShortName(displayShortName)
            .build()
    }
}

internal fun OrganisationUnitGroup.toDB(): OrganisationUnitGroupDB {
    return OrganisationUnitGroupDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        deleted = deleted(),
    )
}
