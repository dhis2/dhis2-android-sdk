package org.hisp.dhis.android.persistence.category


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB

@Entity(
    tableName = "CategoryOptionOrganisationUnitLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = OrganisationUnitDB::class,
            parentColumns = ["uid"],
            childColumns = ["organisationUnit"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["categoryOption", "organisationUnit"], unique = true),
        Index(value = ["categoryOption"]),
        Index(value = ["organisationUnit"]),
    ],
)
internal data class CategoryOptionOrganisationUnitLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val categoryOption: String,
    val organisationUnit: String?,
    val restriction: String?,
)
