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

package org.hisp.dhis.android.core.parser.expression;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.parser.service.ExpressionService;
import org.hisp.dhis.android.core.parser.service.dataobject.DataElementOperandObject;
import org.hisp.dhis.android.core.parser.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ExpressionServiceShould {

    private String dataElementId1 = "sK2wroysTNW";
    private String dataElementId2 = "lZGmxYbs97q";

    private String categoryOptionComboId1 = "tpghB93ks57";
    private String categoryOptionComboId2 = "zDhUuAYrxNC";

    private String constantId = "e19hj1w7yKP";

    private String orgunitGroupId = "RAL7YE4KJ58";

    @Mock
    IdentifiableObjectStore<DataElement> dataElementStore;

    @Mock
    DataElement dataElement1, dataElement2;

    @Mock
    IdentifiableObjectStore<CategoryOptionCombo> categoryOptionComboStore;

    @Mock
    CategoryOptionCombo categoryOptionCombo1, categoryOptionCombo2;

    @Mock
    IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore;

    @Mock
    OrganisationUnitGroup organisationUnitGroup;

    private ExpressionService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new ExpressionService(
                dataElementStore,
                categoryOptionComboStore,
                organisationUnitGroupStore);

        when(dataElementStore.selectByUid(dataElementId1)).thenReturn(dataElement1);
        when(dataElementStore.selectByUid(dataElementId2)).thenReturn(dataElement2);

        when(categoryOptionComboStore.selectByUid(categoryOptionComboId1)).thenReturn(categoryOptionCombo1);
        when(categoryOptionComboStore.selectByUid(categoryOptionComboId2)).thenReturn(categoryOptionCombo2);

        when(organisationUnitGroupStore.selectByUid(orgunitGroupId)).thenReturn(organisationUnitGroup);
    }

    @Test
    public void evaluate_dataelements() {
        String expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + deOperand(dataElementId2, categoryOptionComboId2);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, categoryOptionComboId1), 5.0);
        valueMap.put(DataElementOperandObject.create(dataElementId2, categoryOptionComboId2), 3.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    public void evaluate_constants() {
        String expression = de(dataElementId1) + " + " + constant(constantId);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, null), 5.0);

        Map<String, Constant> constantMap = new HashMap<>();
        constantMap.put(constantId, Constant.builder().uid(constantId).value(4.0).build());

        Double result = (Double) service.getExpressionValue(expression, valueMap, constantMap, Collections.emptyMap(),
                10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(9.0);
    }

    @Test
    public void evaluate_without_coc() {
        String expression = de(dataElementId1) + " + " + de(dataElementId2);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, null), 5.0);
        valueMap.put(DataElementOperandObject.create(dataElementId2, null), 3.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    public void evaluate_days() {
        String expression = de(dataElementId1) + " + [days]";

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, null), 5.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(15.0);
    }

    @Test
    public void evaluate_orgunit_groups() {
        String expression = de(dataElementId1) + " + " + oug(orgunitGroupId);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, null), 5.0);

        Map<String, Integer> orgunitMap = new HashMap<>();
        orgunitMap.put(orgunitGroupId, 20);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                orgunitMap, 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(25.0);
    }

    @Test
    public void evaluate_missing_strategies_with_some_missing_values() {
        String expression = de(dataElementId1) + " + " + de(dataElementId2);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperandObject.create(dataElementId1, null), 5.0);

        Double resultNeverSkip = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(resultNeverSkip).isEqualTo(5.0);

        Double resultSkipIfAny = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING);
        assertThat(resultSkipIfAny).isNull();

        Double resultSkipIfAll = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING);
        assertThat(resultSkipIfAll).isEqualTo(5.0);
    }

    @Test
    public void evaluate_missing_strategies_with_all_missing_values() {
        String expression = de(dataElementId1) + " + " + de(dataElementId2);

        Map<DimensionalItemObject, Double> valueMap = Collections.emptyMap();

        Double resultNeverSkip = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(resultNeverSkip).isEqualTo(0.0);

        Double resultSkipIfAny = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.SKIP_IF_ANY_VALUE_MISSING);
        assertThat(resultSkipIfAny).isNull();

        Double resultSkipIfAll = (Double) service.getExpressionValue(expression, valueMap,
                Collections.emptyMap(), Collections.emptyMap(), 10, MissingValueStrategy.SKIP_IF_ALL_VALUES_MISSING);
        assertThat(resultSkipIfAll).isNull();
    }

    @Test
    public void evaluate_number_comparison() {
        assertThat((Boolean) service.getExpressionValue("5.0 < 8.0")).isTrue();
        assertThat((Boolean) service.getExpressionValue("5.0 < 5.0")).isFalse();

        assertThat((Boolean) service.getExpressionValue("5.0 <= 8.0")).isTrue();
        assertThat((Boolean) service.getExpressionValue("5.0 <= 5.0")).isTrue();

        assertThat((Boolean) service.getExpressionValue("5.0 == 8.0")).isFalse();
        assertThat((Boolean) service.getExpressionValue("5.0 == 5.0")).isTrue();

        assertThat((Boolean) service.getExpressionValue("5.0 != 8.0")).isTrue();
        assertThat((Boolean) service.getExpressionValue("5.0 != 5.0")).isFalse();
    }

    @Test
    public void evaluate_logical_operators() {
        assertThat((Boolean) service.getExpressionValue("true && true")).isTrue();
        assertThat((Boolean) service.getExpressionValue("true and true")).isTrue();

        assertThat((Boolean) service.getExpressionValue("true || false")).isTrue();
        assertThat((Boolean) service.getExpressionValue("true or false")).isTrue();

        assertThat((Boolean) service.getExpressionValue("5.0 == 8.0 && 4.0 == 4.0")).isFalse();
        assertThat((Boolean) service.getExpressionValue("5.0 == 5.0 && 4.0 == 4.0")).isTrue();

        assertThat((Boolean) service.getExpressionValue("5.0 != 8.0 || 5.0 == 8.0")).isTrue();
        assertThat((Boolean) service.getExpressionValue("5.0 != 5.0 || 8.0 != 8.0")).isFalse();
    }

    @Test
    public void get_dataelement_ids() {
        String expression = de(dataElementId1) + " + " + de(dataElementId2);
        Set<DataElementOperand> dataElementOperands = service.getDataElementOperands(expression);

        assertThat(dataElementOperands.size()).isEqualTo(2);
        for (DataElementOperand deo : dataElementOperands) {
            if (!deo.dataElement().uid().equals(dataElementId1) && !deo.dataElement().uid().equals(dataElementId2)) {
                throw new RuntimeException("Should not reach this point");
            }
            assertThat(deo.categoryOptionCombo()).isNull();
        }
    }

    @Test
    public void get_dataelement_operands_ids() {
        String expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + deOperand(dataElementId2, categoryOptionComboId2);
        Set<DataElementOperand> dataElementOperands = service.getDataElementOperands(expression);

        assertThat(dataElementOperands.size()).isEqualTo(2);
        for (DataElementOperand deo : dataElementOperands) {
            if (deo.dataElement().uid().equals(dataElementId1)) {
                assertThat(deo.categoryOptionCombo().uid()).isEqualTo(categoryOptionComboId1);
            } else if (deo.dataElement().uid().equals(dataElementId2)) {
                assertThat(deo.categoryOptionCombo().uid()).isEqualTo(categoryOptionComboId2);
            } else {
                throw new RuntimeException("Should not reach this point");
            }
        }
    }

    @Test
    public void get_dataelement_ids_in_empty_expression() {
        String expression = "[days] + " + constant(constantId) + " + " + oug(constantId);
        Set<DataElementOperand> dataElementOperands = service.getDataElementOperands(expression);

        assertThat(dataElementOperands).isEmpty();
    }

    @Test
    public void get_description_when_all_items_exist() {
        when(dataElement1.displayName()).thenReturn("Data Element 1");
        when(dataElement2.displayName()).thenReturn("Data Element 2");
        when(categoryOptionCombo1.displayName()).thenReturn("COC 1");
        when(categoryOptionCombo2.displayName()).thenReturn("COC 2");

        String expression = deOperand(dataElementId1, categoryOptionComboId1) + " + " + deOperand(dataElementId2, categoryOptionComboId2);

        String description = service.getExpressionDescription(expression, Collections.emptyMap());

        assertThat(description).isEqualTo("Data Element 1 (COC 1) + Data Element 2 (COC 2)");
    }

    @Test
    public void get_description_with_missing_items() {
        when(dataElement1.displayName()).thenReturn("Data Element 1");

        String expression = de(dataElementId1) + " + " + de("atGmxEbs97n");

        String description = service.getExpressionDescription(expression, Collections.emptyMap());

        assertThat(description).isEqualTo("Data Element 1 + " + de("atGmxEbs97n"));
    }

    private String constant(String uid) {
        return "C{" + uid + "}";
    }

    private String de(String uid) {
        return "#{" + uid + "}";
    }

    private String deOperand(String de, String coc) {
        return "#{" + de + "." + coc + "}";
    }

    private String oug(String uid) {
        return "OUG{" + uid + "}";
    }
}
