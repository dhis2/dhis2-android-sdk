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

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.parser.service.ExpressionService;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ExpressionServiceShould {

    private String constantId = "e19hj1w7yKP";

    @Test
    public void evaluate() {
        String expression = "#{sK2wroysTNW.tpghB93ks57} + #{lZGmxYbs97q.zDhUuAYrxNC} + C{" + constantId + "}";

        ExpressionService service = new ExpressionService();

        Map<DataElementOperand, Double> valueMap = new HashMap<>();
        valueMap.put(DataElementOperand.builder().uid("operand1").dataElement(ObjectWithUid.create("sK2wroysTNW.tpghB93ks57")).build(), 5.0);
        valueMap.put(DataElementOperand.builder().uid("operand2").dataElement(ObjectWithUid.create("lZGmxYbs97q.zDhUuAYrxNC")).build(), 3.0);

        Map<String, Constant> constantMap = new HashMap<>();
        constantMap.put(constantId, Constant.builder().uid(constantId).value(4.0).build());

        Double result = service.getExpressionValue(expression, valueMap, constantMap, Collections.emptyMap(), 10, MissingValueStrategy.NEVER_SKIP);
        assertThat(result).isEqualTo(12.0);
    }
}
