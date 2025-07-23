package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "OrganisationUnitProgramLink",
    foreignKeys = [
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["organisationUnit", "program"],
)
internal data class OrganisationUnitProgramLinkDB(
    val organisationUnit: String,
    val program: String,
) : EntityDB<OrganisationUnitProgramLink> {

    override fun toDomain(): OrganisationUnitProgramLink {
        return OrganisationUnitProgramLink.builder()
            .organisationUnit(organisationUnit)
            .program(program)
            .build()
    }
}

internal fun OrganisationUnitProgramLink.toDB(): OrganisationUnitProgramLinkDB {
    return OrganisationUnitProgramLinkDB(
        organisationUnit = organisationUnit()!!,
        program = program()!!,
    )
}
