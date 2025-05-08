package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB

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
    indices = [
        Index(value = ["organisationUnit", "dataSet"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["organisationUnit"]),
    ],
)
internal data class DataSetOrganisationUnitLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val dataSet: String,
    val organisationUnit: String,
)
