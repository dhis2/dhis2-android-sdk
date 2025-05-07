

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Attribute")
internal data class AttributeDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val valueType: String?,
    val uniqueProperty: Int?,
    val mandatory: Int?,
    val indicatorAttribute: Int?,
    val indicatorGroupAttribute: Int?,
    val userGroupAttribute: Int?,
    val dataElementAttribute: Int?,
    val constantAttribute: Int?,
    val categoryOptionAttribute: Int?,
    val optionSetAttribute: Int?,
    val sqlViewAttribute: Int?,
    val legendSetAttribute: Int?,
    val trackedEntityAttributeAttribute: Int?,
    val organisationUnitAttribute: Int?,
    val dataSetAttribute: Int?,
    val documentAttribute: Int?,
    val validationRuleGroupAttribute: Int?,
    val dataElementGroupAttribute: Int?,
    val sectionAttribute: Int?,
    val trackedEntityTypeAttribute: Int?,
    val userAttribute: Int?,
    val categoryOptionGroupAttribute: Int?,
    val programStageAttribute: Int?,
    val programAttribute: Int?,
    val categoryAttribute: Int?,
    val categoryOptionComboAttribute: Int?,
    val categoryOptionGroupSetAttribute: Int?,
    val validationRuleAttribute: Int?,
    val programIndicatorAttribute: Int?,
    val organisationUnitGroupAttribute: Int?,
    val dataElementGroupSetAttribute: Int?,
    val organisationUnitGroupSetAttribute: Int?,
    val optionAttribute: Int?
)
