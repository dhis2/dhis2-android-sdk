package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "DataSetOrganisationUnitLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
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
    primaryKeys = ["organisationUnit", "dataSet"],
)
internal data class DataSetOrganisationUnitLinkDB(
    val dataSet: String,
    @ParentColumn val organisationUnit: String,
) : EntityDB<DataSetOrganisationUnitLink> {

    override fun toDomain(): DataSetOrganisationUnitLink {
        return DataSetOrganisationUnitLink.builder()
            .dataSet(dataSet)
            .organisationUnit(organisationUnit)
            .build()
    }
}

internal fun DataSetOrganisationUnitLink.toDB(): DataSetOrganisationUnitLinkDB {
    return DataSetOrganisationUnitLinkDB(
        dataSet = dataSet()!!,
        organisationUnit = organisationUnit()!!,
    )
}
