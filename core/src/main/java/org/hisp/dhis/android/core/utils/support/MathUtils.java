/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.core.utils.support;

import java.util.regex.Pattern;

/**
 * @author Lars Helge Overland
 */
public final class MathUtils {

    private static final String NUMERIC_REGEXP = "^(-?0|-?[1-9]\\d*)(\\.\\d+)?(E(-)?\\d+)?$";
    private static final Pattern NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEXP);

    private MathUtils() {
        // no instances
    }

    /**
     * Returns a number rounded off to the given number of decimals.
     *
     * @param value    the value to round off.
     * @param decimals the number of decimals.
     * @return a number rounded off to the given number of decimals.
     */
    static double getRounded(double value, int decimals) {
        final double factor = Math.pow(10, decimals);

        return Math.round(value * factor) / factor;
    }

    /**
     * Returns a rounded off number.
     * <p/>
     * <ul>
     * <li>If value is exclusively between 1 and -1 it will have 2 decimals.</li>
     * <li>If value if greater or equal to 1 the value will have 1 decimal.</li>
     * </ul>
     *
     * @param value the value to round off.
     * @return a rounded off number.
     */
    public static double getRounded(double value) {
        if (value < 1d && value > -1d) {
            return getRounded(value, 2);
        } else {
            return getRounded(value, 1);
        }
    }

    /**
     * Returns true if the provided string argument is to be considered numeric.
     *
     * @param value the value.
     * @return true if the provided string argument is to be considered numeric.
     */
    public static boolean isNumeric(String value) {
        return value != null && isDouble(value) && NUMERIC_PATTERN.matcher(value).matches();
    }

    private static boolean isDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
