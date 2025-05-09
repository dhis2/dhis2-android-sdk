package org.hisp.dhis.android.persistence.validation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleExpression
import org.hisp.dhis.android.core.validation.ValidationRuleImportance
import org.hisp.dhis.android.core.validation.ValidationRuleOperator
import org.hisp.dhis.android.persistence.common.IntegerArrayDB

@Entity(
    tableName = "ValidationRule",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class ValidationRuleDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
    val instruction: String?,
    val importance: String?,
    val operator: String?,
    val periodType: String?,
    val skipFormValidation: Boolean?,
    val leftSideExpression: String?,
    val leftSideDescription: String?,
    val leftSideMissingValueStrategy: String?,
    val rightSideExpression: String?,
    val rightSideDescription: String?,
    val rightSideMissingValueStrategy: String?,
    val organisationUnitLevels: String?,
) {
    fun toDomain(): ValidationRule {
        return ValidationRule.builder()
            .uid(uid)
            .code(code)
            .name(name)
            .displayName(displayName)
            .created(created?.toJavaDate())
            .lastUpdated(lastUpdated?.toJavaDate())
            .shortName(shortName)
            .displayShortName(displayShortName)
            .description(description)
            .displayDescription(displayDescription)
            .instruction(instruction)
            .importance(importance?.let { ValidationRuleImportance.valueOf(it) })
            .operator(operator?.let { ValidationRuleOperator.valueOf(it) })
            .periodType(periodType?.let { PeriodType.valueOf(it) })
            .skipFormValidation(skipFormValidation)
            .leftSide(
                ValidationRuleExpression.builder()
                    .expression(leftSideExpression)
                    .description(leftSideDescription)
                    .missingValueStrategy(leftSideMissingValueStrategy?.let { MissingValueStrategy.valueOf(it) })
                    .build()
            )
            .rightSide(
                ValidationRuleExpression.builder()
                    .expression(rightSideExpression)
                    .description(rightSideDescription)
                    .missingValueStrategy(rightSideMissingValueStrategy?.let { MissingValueStrategy.valueOf(it) })
                    .build()
            )
            .organisationUnitLevels(IntegerArrayDB.toDomain(organisationUnitLevels))
            .build()
    }
}

internal fun ValidationRule.toDB(): ValidationRuleDB {
    return ValidationRuleDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created()?.dateFormat(),
        lastUpdated = lastUpdated()?.dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        instruction = instruction(),
        importance = importance()?.name,
        operator = operator()?.name,
        periodType = periodType()?.name,
        skipFormValidation = skipFormValidation(),
        leftSideExpression = leftSide().expression(),
        leftSideDescription = leftSide().description(),
        leftSideMissingValueStrategy = leftSide().missingValueStrategy()?.name,
        rightSideExpression = rightSide().expression(),
        rightSideDescription = rightSide().description(),
        rightSideMissingValueStrategy = rightSide().missingValueStrategy()?.name,
        organisationUnitLevels = IntegerArrayDB.toDB(organisationUnitLevels())
    )
}
