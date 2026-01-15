package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "OrganisationUnitOrganisationUnitGroupLink",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OrganisationUnitGroupDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnitGroup"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["organisationUnit", "organisationUnitGroup"],
)
internal data class OrganisationUnitOrganisationUnitGroupLinkDB(
    @ParentColumn val organisationUnit: String,
    val organisationUnitGroup: String,
) : EntityDB<OrganisationUnitOrganisationUnitGroupLink> {

    override fun toDomain(): OrganisationUnitOrganisationUnitGroupLink {
        return OrganisationUnitOrganisationUnitGroupLink.builder()
            .organisationUnit(organisationUnit)
            .organisationUnitGroup(organisationUnitGroup)
            .build()
    }
}

internal fun OrganisationUnitOrganisationUnitGroupLink.toDB(): OrganisationUnitOrganisationUnitGroupLinkDB {
    return OrganisationUnitOrganisationUnitGroupLinkDB(
        organisationUnit = organisationUnit()!!,
        organisationUnitGroup = organisationUnitGroup()!!,
    )
}
