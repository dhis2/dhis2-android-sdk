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

package org.hisp.dhis.android.core.systeminfo;

public enum DHISVersion {
    V2_29,
    V2_30,
    V2_31,
    V2_32,
    V2_33,
    V2_34,
    V2_35;

    private static final String V2_29_STR = "2.29";
    private static final String V2_30_STR = "2.30";
    private static final String V2_31_STR = "2.31";
    private static final String V2_32_STR = "2.32";
    private static final String V2_33_STR = "2.33";
    private static final String V2_34_STR = "2.34";
    private static final String V2_35_STR = "2.35";

    public static DHISVersion getValue(String versionStr) {
        if (versionStr.startsWith(V2_29_STR)) {
            return V2_29;
        } else if (versionStr.startsWith(V2_30_STR)) {
            return V2_30;
        } else if (versionStr.startsWith(V2_31_STR)) {
            return V2_31;
        } else if (versionStr.startsWith(V2_32_STR)) {
            return V2_32;
        } else if (versionStr.startsWith(V2_33_STR)) {
            return V2_33;
        } else if (versionStr.startsWith(V2_34_STR)) {
            return V2_34;
        } else if (versionStr.startsWith(V2_35_STR)) {
            return V2_35;
        } else {
            return null;
        }
    }

    public static boolean isAllowedVersion(String versionStr) {
        return getValue(versionStr) != null;
    }

    public static String[] allowedVersionsAsStr() {
        return new String[]{V2_29_STR, V2_30_STR, V2_31_STR, V2_32_STR, V2_33_STR, V2_34_STR, V2_35_STR};
    }
}