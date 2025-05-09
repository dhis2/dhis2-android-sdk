package org.hisp.dhis.android.persistence.datavalue

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.period.PeriodDB

@Entity(
    tableName = "DataValue",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = PeriodDB::class,
            parentColumns = ["periodId"],
            childColumns = ["period"],
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
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["attributeOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(
            value = ["dataElement", "period", "organisationUnit", "categoryOptionCombo", "attributeOptionCombo"],
            unique = true,
        ),
        Index(value = ["dataElement"]),
        Index(value = ["period"]),
        Index(value = ["organisationUnit"]),
        Index(value = ["categoryOptionCombo"]),
        Index(value = ["attributeOptionCombo"]),
    ],
)
internal data class DataValueDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataElement: String,
    val period: String,
    val organisationUnit: String,
    val categoryOptionCombo: String,
    val attributeOptionCombo: String,
    val value: String?,
    val storedBy: String?,
    val created: String?,
    val lastUpdated: String?,
    val comment: String?,
    val followUp: Int?,
    val syncState: String?,
    val deleted: Int?,
)
