package org.hisp.dhis.android.persistence.dataelement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.option.OptionSetDB

@Entity(
    tableName = "DataElement",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class DataElementDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    override val deleted: Boolean?,
    val valueType: String?,
    val zeroIsSignificant: Boolean?,
    val aggregationType: String?,
    val formName: String?,
    val domainType: String?,
    val displayFormName: String?,
    val optionSet: String?,
    val categoryCombo: String,
    val fieldMask: String?,
    override val color: String?,
    override val icon: String?,
) : EntityDB<DataElement>, BaseNameableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): DataElement {
        return DataElement.builder().apply {
            applyBaseNameableFields(this@DataElementDB)
            applyStyleFields(this@DataElementDB)
            valueType?.let { valueType(ValueType.valueOf(it)) }
            zeroIsSignificant(zeroIsSignificant)
            aggregationType(aggregationType)
            formName(formName)
            domainType(domainType)
            displayFormName(displayFormName)
            optionSet?.let { optionSet(ObjectWithUidDB(it).toDomain()) }
            categoryCombo(ObjectWithUidDB(categoryCombo).toDomain())
            fieldMask(fieldMask)
        }.build()
    }
}

internal fun DataElement.toDB(): DataElementDB {
    return DataElementDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        valueType = valueType()?.name,
        zeroIsSignificant = zeroIsSignificant(),
        aggregationType = aggregationType(),
        formName = formName(),
        domainType = domainType(),
        displayFormName = displayFormName(),
        optionSet = optionSet()?.uid(),
        categoryCombo = categoryCombo()?.uid() ?: CategoryComboDB.Companion.DEFAULT_UID,
        fieldMask = fieldMask(),
        color = style().color(),
        icon = style().icon(),
        deleted = deleted(),
    )
}
