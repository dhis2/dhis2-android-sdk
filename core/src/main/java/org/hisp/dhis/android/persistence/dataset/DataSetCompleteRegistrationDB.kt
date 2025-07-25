package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.period.PeriodDB

@Entity(
    tableName = "DataSetCompleteRegistration",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
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
            childColumns = ["attributeOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["period", "dataSet", "organisationUnit", "attributeOptionCombo"],
)
internal data class DataSetCompleteRegistrationDB(
    val period: String,
    val dataSet: String,
    val organisationUnit: String,
    val attributeOptionCombo: String,
    val date: String?,
    val storedBy: String?,
    override val syncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<DataSetCompleteRegistration>, DeletableObjectDB, DataObjectDB {

    override fun toDomain(): DataSetCompleteRegistration {
        return DataSetCompleteRegistration.builder().apply {
            period(period)
            dataSet(dataSet)
            organisationUnit(organisationUnit)
            attributeOptionCombo(attributeOptionCombo)
            date(date.toJavaDate())
            storedBy(storedBy)
            syncState(syncState?.toDomain())
            deleted(deleted)
        }.build()
    }
}

internal fun DataSetCompleteRegistration.toDB(): DataSetCompleteRegistrationDB {
    return DataSetCompleteRegistrationDB(
        period = period()!!,
        dataSet = dataSet()!!,
        organisationUnit = organisationUnit()!!,
        attributeOptionCombo = attributeOptionCombo(),
        date = date().dateFormat(),
        storedBy = storedBy(),
        syncState = syncState()?.toDB(),
        deleted = deleted(),
    )
}
