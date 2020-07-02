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

import org.apache.commons.lang3.math.NumberUtils;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementObject;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DataElementOperandObject;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ValidationEngineHelper {

    private ValidationEngineHelper() {
    }

    static Map<DimensionalItemObject, Double> getValueMap(List<DataValue> dataValues) {
        Map<DimensionalItemObject, Double> valueMap = new HashMap<>();

        for (DataValue dataValue : dataValues) {
            String deId = dataValue.dataElement();
            String cocId = dataValue.categoryOptionCombo();

            DimensionalItemObject dataElementItem = DataElementObject.create(deId);
            addDimensionalItemValueToMap(dataElementItem, dataValue.value(), valueMap);

            DimensionalItemObject dataElementOperandItem = DataElementOperandObject.create(deId, cocId);
            addDimensionalItemValueToMap(dataElementOperandItem, dataValue.value(), valueMap);
        }
        return valueMap;
    }

    private static void addDimensionalItemValueToMap(DimensionalItemObject item,
                                                     String value,
                                                     Map<DimensionalItemObject, Double> valueMap) {
        Double existingValue = valueMap.get(item);
        Double newValue = NumberUtils.createDouble(value);

        Double result = existingValue == null ? newValue : existingValue + newValue;

        valueMap.put(item, result);
    }
}