/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.validation

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.validation.internal.ValidationRuleStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class ValidationRuleCollectionRepository internal constructor(
    store: ValidationRuleStore,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<ValidationRule, ValidationRuleCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ValidationRuleCollectionRepository(
            store,
            s,
        )
    },
) {
    fun byInstruction(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.INSTRUCTION)
    }

    fun byImportance(): EnumFilterConnector<ValidationRuleCollectionRepository, ValidationRuleImportance> {
        return cf.enumC(ValidationRuleTableInfo.Columns.IMPORTANCE)
    }

    fun byOperator(): EnumFilterConnector<ValidationRuleCollectionRepository, ValidationRuleOperator> {
        return cf.enumC(ValidationRuleTableInfo.Columns.OPERATOR)
    }

    fun byPeriodType(): EnumFilterConnector<ValidationRuleCollectionRepository, PeriodType> {
        return cf.enumC(ValidationRuleTableInfo.Columns.PERIOD_TYPE)
    }

    fun bySkipFormValidation(): BooleanFilterConnector<ValidationRuleCollectionRepository> {
        return cf.bool(ValidationRuleTableInfo.Columns.SKIP_FORM_VALIDATION)
    }

    fun byLeftSideExpression(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.LEFT_SIDE_EXPRESSION)
    }

    fun byLeftSideDescription(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.LEFT_SIDE_DESCRIPTION)
    }

    fun byLeftSideMissingValueStrategy(): EnumFilterConnector<
        ValidationRuleCollectionRepository,
        MissingValueStrategy,
        > {
        return cf.enumC(ValidationRuleTableInfo.Columns.LEFT_SIDE_MISSING_VALUE_STRATEGY)
    }

    fun byRightSideExpression(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.RIGHT_SIDE_EXPRESSION)
    }

    fun byRightSideDescription(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.RIGHT_SIDE_DESCRIPTION)
    }

    fun byRightSideMissingValueStrategy(): EnumFilterConnector<
        ValidationRuleCollectionRepository,
        MissingValueStrategy,
        > {
        return cf.enumC(ValidationRuleTableInfo.Columns.RIGHT_SIDE_MISSING_VALUE_STRATEGY)
    }

    fun byOrganisationUnitLevels(): StringFilterConnector<ValidationRuleCollectionRepository> {
        return cf.string(ValidationRuleTableInfo.Columns.ORGANISATION_UNIT_LEVELS)
    }

    fun byDataSetUids(dataSetUids: List<String>): ValidationRuleCollectionRepository {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
            DataSetValidationRuleLinkTableInfo.TABLE_INFO.name(),
            DataSetValidationRuleLinkTableInfo.Columns.VALIDATION_RULE,
            DataSetValidationRuleLinkTableInfo.Columns.DATA_SET,
            dataSetUids,
        )
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ValidationRule> = emptyMap()
    }
}
