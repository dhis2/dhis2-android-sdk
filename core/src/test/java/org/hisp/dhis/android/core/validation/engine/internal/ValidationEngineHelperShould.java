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

package org.hisp.dhis.android.core.validation.engine.internal;

import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementObject;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementOperandObject;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.validation.engine.internal.ValidationEngineHelper.getValueMap;

public class ValidationEngineHelperShould {

    private String dataElement1 = "sK2wroysTNW";
    private String dataElement2 = "lZGmxYbs97q";

    private String coc1 = "tpghB93ks57";
    private String coc2 = "zDhUuAYrxNC";
    private String cocDefault = "HllvX50cXC0";

    @Test
    public void map_data_elements_only() {
        List<DataValue> dataValues = new ArrayList<>();

        dataValues.add(dv(dataElement1, cocDefault, "4"));
        dataValues.add(dv(dataElement2, cocDefault, "6"));

        Map<DimensionalItemObject, Double> valueMap = getValueMap(dataValues);

        assertThat(valueMap.keySet().size()).isEqualTo(4);
        assertContainsEntry(valueMap, dataElement1, 4.0);
        assertContainsEntry(valueMap, dataElement1, cocDefault, 4.0);
        assertContainsEntry(valueMap, dataElement2, 6.0);
        assertContainsEntry(valueMap, dataElement2, cocDefault, 6.0);
    }

    @Test
    public void map_data_elements_and_operands() {
        List<DataValue> dataValues = new ArrayList<>();

        dataValues.add(dv(dataElement1, coc1, "4"));
        dataValues.add(dv(dataElement1, coc2, "5"));
        dataValues.add(dv(dataElement2, coc1, "6"));
        dataValues.add(dv(dataElement2, coc2, "7"));

        Map<DimensionalItemObject, Double> valueMap = getValueMap(dataValues);

        assertThat(valueMap.keySet().size()).isEqualTo(6);
        assertContainsEntry(valueMap, dataElement1, 9.0);
        assertContainsEntry(valueMap, dataElement1, coc1, 4.0);
        assertContainsEntry(valueMap, dataElement1, coc2, 5.0);
        assertContainsEntry(valueMap, dataElement2, 13.0);
        assertContainsEntry(valueMap, dataElement2, coc1, 6.0);
        assertContainsEntry(valueMap, dataElement2, coc2, 7.0);
    }

    private DataValue dv(String dataElementId, String categoryOptionComboId, String value) {
        return DataValue.builder()
                .dataElement(dataElementId)
                .categoryOptionCombo(categoryOptionComboId)
                .value(value)
                .build();
    }

    private void assertContainsEntry(Map<DimensionalItemObject, Double> valueMap,
                                     String dataElementId,
                                     Double value) {
        assertContainsEntry(valueMap, dataElementId, null, value);
    }

    private void assertContainsEntry(Map<DimensionalItemObject, Double> valueMap,
                                     String dataElementId,
                                     String categoryOptionComboId,
                                     Double value) {
        Map.Entry<DimensionalItemObject, Double> entry = null;
        DimensionalItemObject key = categoryOptionComboId == null ?
                DataElementObject.create(dataElementId) :
                DataElementOperandObject.create(dataElementId, categoryOptionComboId);

        for (Map.Entry<DimensionalItemObject, Double> e : valueMap.entrySet()) {
            if (e.getKey().equals(key)) {
                entry = e;
                break;
            }
        }

        assertThat(entry).isNotNull();
        assertThat(entry.getValue()).isEqualTo(value);
    }
}