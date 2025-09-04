package org.hisp.dhis.android.persistence.dataapproval

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataapproval.DataApproval
import org.hisp.dhis.android.core.dataapproval.DataApprovalState
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitDB
import org.hisp.dhis.android.persistence.period.PeriodDB

@Entity(
    tableName = "DataApproval",
    foreignKeys = [
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["attributeOptionCombo"],
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
    ],
    primaryKeys = ["workflow", "attributeOptionCombo", "period", "organisationUnit"],
)
internal data class DataApprovalDB(
    val workflow: String,
    val organisationUnit: String,
    val period: String,
    val attributeOptionCombo: String,
    val state: String?,
) : EntityDB<DataApproval> {

    override fun toDomain(): DataApproval {
        return DataApproval.builder().apply {
            workflow(workflow)
            organisationUnit(organisationUnit)
            period(period)
            attributeOptionCombo(attributeOptionCombo)
            state?.let { state(DataApprovalState.valueOf(it)) }
        }.build()
    }
}

internal fun DataApproval.toDB(): DataApprovalDB {
    return DataApprovalDB(
        workflow = workflow()!!,
        organisationUnit = organisationUnit()!!,
        period = period()!!,
        attributeOptionCombo = attributeOptionCombo()!!,
        state = state()?.name,
    )
}
