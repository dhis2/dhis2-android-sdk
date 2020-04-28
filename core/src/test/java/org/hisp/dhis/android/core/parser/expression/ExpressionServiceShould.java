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

import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.parser.service.ExpressionService;
import org.hisp.dhis.android.core.parser.service.dataobject.DataElementOperandObject;
import org.hisp.dhis.android.core.parser.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ExpressionServiceShould {

    private String dataElement1 = "sK2wroysTNW";
    private String dataElement2 = "lZGmxYbs97q";

    private String coc1 = "tpghB93ks57";
    private String coc2 = "zDhUuAYrxNC";

    private String constantId = "e19hj1w7yKP";

    private String orgunitGroupId = "RAL7YE4KJ58";

    private ExpressionService service;

    @Before
    public void setUp() {
        service = new ExpressionService();
    }

    @Test
    public void evaluate_dataelements() {
        String expression = deOperand(dataElement1, coc1) + " + " + deOperand(dataElement2, coc2);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(new DataElementOperandObject(dataElement1, coc1), 5.0);
        valueMap.put(new DataElementOperandObject(dataElement2, coc2), 3.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    public void evaluate_constants() {
        String expression = de(dataElement1) + " + " + constant(constantId);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(new DataElementOperandObject(dataElement1, null), 5.0);

        Map<String, Constant> constantMap = new HashMap<>();
        constantMap.put(constantId, Constant.builder().uid(constantId).value(4.0).build());

        Double result = (Double) service.getExpressionValue(expression, valueMap, constantMap, Collections.emptyMap(),
                10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(9.0);
    }

    @Test
    public void evaluate_without_coc() {
        String expression = de(dataElement1) + " + " + de(dataElement2);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(new DataElementOperandObject(dataElement1, null), 5.0);
        valueMap.put(new DataElementOperandObject(dataElement2, null), 3.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    public void evaluate_days() {
        String expression = de(dataElement1) + " + [days]";

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(new DataElementOperandObject(dataElement1, null), 5.0);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(15.0);
    }

    @Test
    public void evaluate_orgunit_groups() {
        String expression = de(dataElement1) + " + " + oug(orgunitGroupId);

        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();
        valueMap.put(new DataElementOperandObject(dataElement1, null), 5.0);

        Map<String, Integer> orgunitMap = new HashMap<>();
        orgunitMap.put(orgunitGroupId, 20);

        Double result = (Double) service.getExpressionValue(expression, valueMap, Collections.emptyMap(),
                orgunitMap, 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(25.0);
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
