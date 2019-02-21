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

package org.hisp.dhis.android.core.utils.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionUtilsShould {

    @Test
    public void evaluate_one_value() throws Exception {
        assertThat(evaluateToString("2.3")).isEqualTo("2.3");
    }

    @Test
    public void evaluate_base_mathematical_operations() throws Exception {
        assertThat(evaluateToString("2.3 + 4")).isEqualTo("6.3");
        assertThat(evaluateToString("4.3 - 2")).isEqualTo("2.3");
        assertThat(evaluateToString("4.3 * 2")).isEqualTo("8.6");
        assertThat(evaluateToString("0.0 * 5")).isEqualTo("0.0");
        assertThat(evaluateToString("7.5 / 2")).isEqualTo("3.75");
    }

    @Test
    public void evaluate_operations_with_parenthesis() throws Exception {
        assertThat(evaluateToString("(2.3 + 4) * 2")).isEqualTo("12.6");
        assertThat(evaluateToString("6 / (2 - 0.5)")).isEqualTo("4.0");
    }

    @Test
    public void evaluate_strings() {
        assertThat(evaluateToString("\"text-value\"")).isEqualTo("text-value");
        assertThat(evaluateToString("\"text-value\" + \"-\" + 1")).isEqualTo("text-value-1");
    }

    private String evaluateToString(String expression) {
        return ExpressionUtils.evaluateToString(expression, null);
    }

    @Test
    public void evaluate_one_value_to_double() throws Exception {
        assertThat(evaluateToDouble("2.3")).isEqualTo(2.3);
    }

    @Test
    public void evaluate_base_mathematical_operations_to_double() throws Exception {
        assertThat(evaluateToDouble("2.3 + 4")).isEqualTo(6.3);
        assertThat(evaluateToDouble("4.3 - 2")).isEqualTo(2.3);
        assertThat(evaluateToDouble("4.3 * 2")).isEqualTo(8.6);
        assertThat(evaluateToDouble("0.0 * 5")).isEqualTo(0.0);
        assertThat(evaluateToDouble("7.5 / 2")).isEqualTo(3.75);
    }

    @Test
    public void evaluate_operations_with_parenthesis_to_double() throws Exception {
        assertThat(evaluateToDouble("(2.3 + 4) * 2")).isEqualTo(12.6);
        assertThat(evaluateToDouble("6 / (2 - 0.5)")).isEqualTo(4.0);
    }

    @Test(expected = IllegalStateException.class)
    public void evaluate_strings_to_double() {
        assertThat(evaluateToDouble("\"text-value\"")).isEqualTo("text-value");
    }

    private Double evaluateToDouble(String expression) {
        return ExpressionUtils.evaluateToDouble(expression, null);
    }

}