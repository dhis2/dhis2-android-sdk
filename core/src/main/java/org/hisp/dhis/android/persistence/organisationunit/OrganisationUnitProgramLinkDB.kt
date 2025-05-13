package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["organisationUnit", "program"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["program"]),
    ],
)
internal data class OrganisationUnitProgramLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val organisationUnit: String,
    val program: String,
) : EntityDB<OrganisationUnitProgramLink> {

    override fun toDomain(): OrganisationUnitProgramLink {
        return OrganisationUnitProgramLink.builder()
            .id(id?.toLong())
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
