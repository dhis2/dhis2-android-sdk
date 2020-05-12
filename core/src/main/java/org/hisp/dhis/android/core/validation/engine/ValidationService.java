/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.validation.engine;

import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.ConstantCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo;
import org.hisp.dhis.android.core.parser.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodCollectionRepository;
import org.hisp.dhis.android.core.validation.ValidationResult;
import org.hisp.dhis.android.core.validation.ValidationResultViolation;
import org.hisp.dhis.android.core.validation.ValidationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ValidationService {

    private final ValidationExecutor validationExecutor;

    private final DataValueCollectionRepository dataValueRepository;

    private final DataSetCollectionRepository dataSetRepository;

    private final ConstantCollectionRepository constantRepository;

    private final PeriodCollectionRepository periodRepository;

    private final LinkStore<OrganisationUnitOrganisationUnitGroupLink> orgunitGroupLinkStore;

    @Inject
    ValidationService(ValidationExecutor validationExecutor,
                      DataValueCollectionRepository dataValueRepository,
                      DataSetCollectionRepository dataSetRepository,
                      ConstantCollectionRepository constantRepository,
                      PeriodCollectionRepository periodRepository,
                      LinkStore<OrganisationUnitOrganisationUnitGroupLink> orgunitGroupLinkStore) {
        this.validationExecutor = validationExecutor;
        this.dataValueRepository = dataValueRepository;
        this.dataSetRepository = dataSetRepository;
        this.constantRepository = constantRepository;
        this.periodRepository = periodRepository;
        this.orgunitGroupLinkStore = orgunitGroupLinkStore;
    }

    public ValidationResult validate(String dataSetUid, String attributeOptionComboUid,
                                     String orgUnitUid, String periodId) {
        List<ValidationRule> rules = getValidationRulesByDataSet();
        List<ValidationResultViolation> violations = new ArrayList<>();

        if (!rules.isEmpty()) {
            Map<String, Constant> constantMap = getConstantMap();
            Map<DimensionalItemObject, Double> valueMap = getValueMap(dataSetUid, attributeOptionComboUid,
                    orgUnitUid, periodId);
            Map<String, Integer> orgunitGroupMap = getOrgunitGroupMap();
            Integer days = getDays(periodId);

            for (ValidationRule rule : rules) {
                violations.addAll(validationExecutor.evaluateRule(rule, valueMap, constantMap, orgunitGroupMap, days));
            }
        }

        ValidationResult.ValidationResultStatus status = violations.isEmpty() ?
                ValidationResult.ValidationResultStatus.OK :
                ValidationResult.ValidationResultStatus.ERROR;

        return ValidationResult.builder()
                .status(status)
                .dataSetUid(dataSetUid)
                .period(periodId)
                .organisationUnitUid(orgUnitUid)
                .attributeOptionComboUid(attributeOptionComboUid)
                .violations(violations)
                .build();
    }

    private List<ValidationRule> getValidationRulesByDataSet() {
        // TODO
        return Collections.emptyList();
    }

    private Map<DimensionalItemObject, Double> getValueMap(String dataSetUid, String attributeOptionComboUid,
                                                           String orgUnitUid, String periodId) {
        DataSet dataSet = dataSetRepository
                .byUid().eq(dataSetUid)
                .withDataSetElements()
                .one().blockingGet();

        List<String> dataElementUids = new ArrayList<>();
        if (dataSet != null && dataSet.dataSetElements() != null) {
            for (DataSetElement dataSetElement : dataSet.dataSetElements()) {
                dataElementUids.add(dataSetElement.dataElement().uid());
            }
        }

        List<DataValue> dataValues = dataValueRepository
                .byDataElementUid().in(dataElementUids)
                .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                .byOrganisationUnitUid().eq(orgUnitUid)
                .byPeriod().eq(periodId)
                .byDeleted().isFalse()
                .blockingGet();

        return ValidationServiceHelper.getValueMap(dataValues);
    }

    private Map<String, Constant> getConstantMap() {
        List<Constant> constants = constantRepository.blockingGet();
        Map<String, Constant> constantMap = new HashMap<>();
        for (Constant constant : constants) {
            constantMap.put(constant.uid(), constant);
        }
        return constantMap;
    }

    private Map<String, Integer> getOrgunitGroupMap() {
        return orgunitGroupLinkStore.groupAndGetCountBy(
                OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT_GROUP);
    }

    private Integer getDays(String periodId) {
        Period period = periodRepository.byPeriodId().eq(periodId).one().blockingGet();
        long diff = period.endDate().getTime() - period.startDate().getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}