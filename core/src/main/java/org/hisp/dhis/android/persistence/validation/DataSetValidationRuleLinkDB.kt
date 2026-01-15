package org.hisp.dhis.android.persistence.validation

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.validation.DataSetValidationRuleLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataset.DataSetDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "DataSetValidationRuleLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ValidationRuleDB::class,
            parentColumns = ["uid"],
            childColumns = ["validationRule"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["dataSet", "validationRule"],
)
internal data class DataSetValidationRuleLinkDB(
    @ParentColumn val dataSet: String,
    val validationRule: String,
) : EntityDB<DataSetValidationRuleLink> {

    override fun toDomain(): DataSetValidationRuleLink {
        return DataSetValidationRuleLink.builder()
            .dataSet(dataSet)
            .validationRule(validationRule)
            .build()
    }
}

internal fun DataSetValidationRuleLink.toDB(): DataSetValidationRuleLinkDB {
    return DataSetValidationRuleLinkDB(
        dataSet = dataSet()!!,
        validationRule = validationRule()!!,
    )
}
