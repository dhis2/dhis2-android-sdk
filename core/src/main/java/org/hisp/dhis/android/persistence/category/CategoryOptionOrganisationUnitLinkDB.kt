package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.category.CategoryOptionOrganisationUnitLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB

@Entity(
    tableName = "CategoryOptionOrganisationUnitLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["categoryOption", "organisationUnit"],
)
internal data class CategoryOptionOrganisationUnitLinkDB(
    val categoryOption: String,
    val organisationUnit: String?,
    val restriction: String?,
) : EntityDB<CategoryOptionOrganisationUnitLink> {

    override fun toDomain(): CategoryOptionOrganisationUnitLink {
        return CategoryOptionOrganisationUnitLink.builder()
            .categoryOption(categoryOption)
            .organisationUnit(organisationUnit)
            .restriction(restriction)
            .build()
    }
}

internal fun CategoryOptionOrganisationUnitLink.toDB(): CategoryOptionOrganisationUnitLinkDB {
    return CategoryOptionOrganisationUnitLinkDB(
        categoryOption = categoryOption(),
        organisationUnit = organisationUnit(),
        restriction = restriction(),
    )
}
