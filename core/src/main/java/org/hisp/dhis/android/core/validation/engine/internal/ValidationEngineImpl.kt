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
package org.hisp.dhis.android.core.validation.engine.internal

import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.ConstantCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitOrganisationUnitGroupLinkStore
import org.hisp.dhis.android.core.parser.internal.service.ExpressionServiceContext
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.parser.internal.service.utils.ExpressionHelper.getValueMap
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleCollectionRepository
import org.hisp.dhis.android.core.validation.engine.ValidationEngine
import org.hisp.dhis.android.core.validation.engine.ValidationResult
import org.hisp.dhis.android.core.validation.engine.ValidationResult.ValidationResultStatus
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class ValidationEngineImpl(
    private val validationExecutor: ValidationExecutor,
    private val validationRuleRepository: ValidationRuleCollectionRepository,
    private val dataValueRepository: DataValueCollectionRepository,
    private val constantRepository: ConstantCollectionRepository,
    private val organisationUnitRepository: OrganisationUnitCollectionRepository,
    private val periodHelper: PeriodHelper,
    private val orgunitGroupLinkStore: OrganisationUnitOrganisationUnitGroupLinkStore,
) : ValidationEngine {
    override suspend fun suspendValidate(
        dataSetUid: String,
        periodId: String,
        orgUnitUid: String,
        attributeOptionComboUid: String,
    ): ValidationResult {
        val rules = suspendGetValidationRulesForDataSetValidation(dataSetUid)

        val violations = if (rules.isNotEmpty()) {
            val constantMap = getConstantMap()
            val valueMap = suspendGetValueMap(
                dataSetUid,
                attributeOptionComboUid,
                orgUnitUid,
                periodId,
            )
            val orgunitGroupMap = getOrgunitGroupMap()
            val organisationUnit = getOrganisationUnit(orgUnitUid)
            val period = getPeriod(periodId)
            val context = ExpressionServiceContext(valueMap, constantMap, orgunitGroupMap, PeriodHelper.getDays(period))

            rules.mapNotNull {
                validationExecutor.evaluateRule(it, organisationUnit, context, period, attributeOptionComboUid)
            }
        } else {
            emptyList()
        }

        val status = if (violations.isEmpty()) ValidationResultStatus.OK else ValidationResultStatus.ERROR

        return ValidationResult.builder()
            .status(status)
            .violations(violations)
            .build()
    }

    private suspend fun suspendGetValidationRulesForDataSetValidation(datasetUid: String): List<ValidationRule> {
        return validationRuleRepository
            .byDataSetUids(listOf(datasetUid))
            .bySkipFormValidation().isFalse
            .suspendGet()
    }

    private suspend fun suspendGetValueMap(
        dataSetUid: String,
        attributeOptionComboUid: String,
        orgUnitUid: String,
        periodId: String,
    ): Map<DimensionalItemObject, Double> {
        val dataValues = dataValueRepository
            .byDataSetUid(dataSetUid)
            .byAttributeOptionComboUid().eq(attributeOptionComboUid)
            .byOrganisationUnitUid().eq(orgUnitUid)
            .byPeriod().eq(periodId)
            .byDeleted().isFalse
            .suspendGet()
        return getValueMap(dataValues)
    }

    private suspend fun getConstantMap(): Map<String, Constant> {
        val constants = constantRepository.suspendGet()
        return mapByUid(constants)
    }

    private suspend fun getOrganisationUnit(orgunitId: String): OrganisationUnit? {
        return organisationUnitRepository.uid(orgunitId).suspendGet()
    }

    private suspend fun getOrgunitGroupMap(): Map<String, Int> {
        return orgunitGroupLinkStore.groupAndGetCountBy(
            OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT_GROUP,
        )
    }

    private fun getPeriod(periodId: String): Period {
        return periodHelper.blockingGetPeriodForPeriodId(periodId)
    }
}
