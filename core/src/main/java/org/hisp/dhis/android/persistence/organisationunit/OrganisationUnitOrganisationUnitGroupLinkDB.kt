package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [
        Index(value = ["organisationUnit", "organisationUnitGroup"], unique = true),
        Index(value = ["organisationUnit"]),
        Index(value = ["organisationUnitGroup"]),
    ],
)
internal data class OrganisationUnitOrganisationUnitGroupLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val organisationUnit: String,
    val organisationUnitGroup: String,
)
