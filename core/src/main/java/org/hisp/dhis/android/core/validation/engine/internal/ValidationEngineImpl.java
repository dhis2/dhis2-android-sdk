/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.validation.engine.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.ConstantCollectionRepository;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.parser.internal.service.utils.ExpressionHelper;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.internal.PeriodHelper;
import org.hisp.dhis.android.core.validation.ValidationRule;
import org.hisp.dhis.android.core.validation.ValidationRuleCollectionRepository;
import org.hisp.dhis.android.core.validation.engine.ValidationEngine;
import org.hisp.dhis.android.core.validation.engine.ValidationResult;
import org.hisp.dhis.android.core.validation.engine.ValidationResultViolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

class ValidationEngineImpl implements ValidationEngine {

    private final ValidationExecutor validationExecutor;

    private final ValidationRuleCollectionRepository validationRuleRepository;

    private final DataValueCollectionRepository dataValueRepository;

    private final ConstantCollectionRepository constantRepository;

    private final OrganisationUnitCollectionRepository organisationUnitRepository;

    private final PeriodHelper periodHelper;

    private final LinkStore<OrganisationUnitOrganisationUnitGroupLink> orgunitGroupLinkStore;

    @Inject
    ValidationEngineImpl(ValidationExecutor validationExecutor,
                         ValidationRuleCollectionRepository validationRuleRepository,
                         DataValueCollectionRepository dataValueRepository,
                         ConstantCollectionRepository constantRepository,
                         OrganisationUnitCollectionRepository organisationUnitRepository,
                         PeriodHelper periodHelper,
                         LinkStore<OrganisationUnitOrganisationUnitGroupLink> orgunitGroupLinkStore) {
        this.validationExecutor = validationExecutor;
        this.validationRuleRepository = validationRuleRepository;
        this.dataValueRepository = dataValueRepository;
        this.constantRepository = constantRepository;
        this.organisationUnitRepository = organisationUnitRepository;
        this.periodHelper = periodHelper;
        this.orgunitGroupLinkStore = orgunitGroupLinkStore;
    }

    @Override
    public Single<ValidationResult> validate(String dataSetUid, String periodId,
                                             String orgUnitUid, String attributeOptionComboUid) {
        return Single.fromCallable(() ->
                blockingValidate(dataSetUid, periodId, orgUnitUid,  attributeOptionComboUid));
    }

    @Override
    public ValidationResult blockingValidate(String dataSetUid, String periodId,
                                             String orgUnitUid, String attributeOptionComboUid) {

        List<ValidationRule> rules = getValidationRulesForDataSetValidation(dataSetUid);
        List<ValidationResultViolation> violations = new ArrayList<>();

        if (!rules.isEmpty()) {
            Map<String, Constant> constantMap = getConstantMap();
            Map<DimensionalItemObject, Double> valueMap = getValueMap(dataSetUid, attributeOptionComboUid,
                    orgUnitUid, periodId);
            Map<String, Integer> orgunitGroupMap = getOrgunitGroupMap();
            OrganisationUnit organisationUnit = getOrganisationUnit(orgUnitUid);
            Period period = getPeriod(periodId);

            for (ValidationRule rule : rules) {
                violations.addAll(validationExecutor.evaluateRule(rule, organisationUnit, valueMap, constantMap,
                        orgunitGroupMap, period, attributeOptionComboUid));
            }
        }

        ValidationResult.ValidationResultStatus status = violations.isEmpty() ?
                ValidationResult.ValidationResultStatus.OK :
                ValidationResult.ValidationResultStatus.ERROR;

        return ValidationResult.builder()
                .status(status)
                .violations(violations)
                .build();
    }

    private List<ValidationRule> getValidationRulesForDataSetValidation(String datasetUid) {
        return validationRuleRepository
                .byDataSetUids(Collections.singletonList(datasetUid))
                .bySkipFormValidation().isFalse()
                .blockingGet();
    }

    private Map<DimensionalItemObject, Double> getValueMap(String dataSetUid, String attributeOptionComboUid,
                                                           String orgUnitUid, String periodId) {
        List<DataValue> dataValues = dataValueRepository
                .byDataSetUid(dataSetUid)
                .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                .byOrganisationUnitUid().eq(orgUnitUid)
                .byPeriod().eq(periodId)
                .byDeleted().isFalse()
                .blockingGet();

        return ExpressionHelper.getValueMap(dataValues);
    }

    private Map<String, Constant> getConstantMap() {
        List<Constant> constants = constantRepository.blockingGet();
        return UidsHelper.mapByUid(constants);
    }

    private OrganisationUnit getOrganisationUnit(String orgunitId) {
        return organisationUnitRepository.uid(orgunitId).blockingGet();
    }

    private Map<String, Integer> getOrgunitGroupMap() {
        return orgunitGroupLinkStore.groupAndGetCountBy(
                OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT_GROUP);
    }

    private Period getPeriod(String periodId) {
        return periodHelper.blockingGetPeriodForPeriodId(periodId);
    }
}