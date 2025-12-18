package org.hisp.dhis.android.persistence.datavalue

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB
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
    primaryKeys = ["dataElement", "period", "organisationUnit", "categoryOptionCombo", "attributeOptionCombo"],
)
internal data class DataValueDB(
    val dataElement: String,
    val period: String,
    val organisationUnit: String,
    val categoryOptionCombo: String,
    val attributeOptionCombo: String,
    val dataSet: String?,
    val value: String?,
    val storedBy: String?,
    val created: String?,
    val lastUpdated: String?,
    val comment: String?,
    val followUp: Boolean?,
    override val syncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<DataValue>, DataObjectDB, DeletableObjectDB {

    override fun toDomain(): DataValue {
        return DataValue.builder().apply {
            dataElement(dataElement)
            period(period)
            organisationUnit(organisationUnit)
            categoryOptionCombo(categoryOptionCombo)
            attributeOptionCombo(attributeOptionCombo)
            dataSet(dataSet)
            value(value)
            storedBy(storedBy)
            created?.let { created(it.toJavaDate()!!) }
            lastUpdated?.let { lastUpdated(it.toJavaDate()!!) }
            comment(comment)
            followUp(followUp)
            syncState?.let { syncState(it.toDomain()) }
            deleted(deleted)
        }.build()
    }
}

internal fun DataValue.toDB(): DataValueDB {
    return DataValueDB(
        dataElement = dataElement()!!,
        period = period()!!,
        organisationUnit = organisationUnit()!!,
        categoryOptionCombo = categoryOptionCombo()!!,
        attributeOptionCombo = attributeOptionCombo()!!,
        dataSet = dataSet(),
        value = value(),
        storedBy = storedBy(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        comment = comment(),
        followUp = followUp(),
        syncState = syncState()?.toDB(),
        deleted = deleted(),
    )
}
