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

import java.util.regex.Matcher;

/**
 * @author Lars Helge Overland
 */
public final class TextUtils {

    private TextUtils() {
        // no instances
    }

    /**
     * Null-safe method for writing the items of a string array out as a string
     * separated by the given char separator.
     *
     * @param array     the array.
     * @param separator the separator of the array items.
     * @return a string.
     */
    public static String toString(String[] array, String separator) {
        StringBuilder builder = new StringBuilder();

        if (array != null && array.length > 0) {
            for (String string : array) {
                builder.append(string).append(separator);
            }

            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    /**
     * Returns the string representation of the object, or null if the object is
     * null.
     *
     * @param object the object.
     * @return the string representation.
     */
    public static String toString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Invokes append tail on matcher with the given string buffer, and returns
     * the string buffer as a string.
     *
     * @param matcher the matcher.
     * @param sb      the string buffer.
     * @return a string.
     */
    public static String appendTail(Matcher matcher, StringBuffer sb) {
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Returns a quoted string (uses doubles quotes), or keep it unchanged if the string is numeric.
     *
     * @param value the input string.
     * @return the quoted string.
     */
    public static String quote(String value) {
        return MathUtils.isNumeric(value) ? value : "\"" + value.replace("\"", "\\\"") + "\"";
    }

    /**
     * Takes a double and returns a string without the decimal part if it's all zeros.
     *
     * @param value the input numeric value.
     * @return a string.
     */
    public static String fromDouble(Double value) {
        if (value != null && !Double.isNaN(value)) {
            value = MathUtils.getRounded(value, 2);
            return String.valueOf(value).replaceAll("\\.0+$", "");
        }

        return "";
    }
}
