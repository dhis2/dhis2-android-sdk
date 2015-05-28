/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.utils;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class Utils {

    private static final String CLASS_TAG = "Utils";
    private static final String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString();
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final int getDpPx(int dp, DisplayMetrics displayMetrics) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                displayMetrics);
        return px;
    }

    public static String removeTimeFromDateString(String dateTime) {
        if(dateTime==null) return null;
        DateTime dt = new DateTime(dateTime);
        return dt.toLocalDate().toString();
    }

    public static String getTempUid() {
        return Dhis2.QUEUED + UUID.randomUUID().toString();
    }

    /**
     * Used to determine if a uid for a modifiable data model is local (haven't gotten a UID from
     * server yet) or if it has.
     * @param uid
     * @return
     */
    public static boolean isLocal(String uid) {
        if(uid == null || uid.length() == randomUUID.length())
            return true;
        else return false;
    }
}
