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

package org.hisp.dhis.android.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.joda.time.DateTime;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public final class DateTimeManager {
    private static final String PREFERENCES = "preferences:lastUpdated";
    private static final String METADATA_UPDATE_DATETIME = "key:metaDataUpdateDateTime";

    private static DateTimeManager mPreferences;
    private final SharedPreferences mPrefs;

    private DateTimeManager(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        mPreferences = new DateTimeManager(context);
    }

    public static DateTimeManager getInstance() {
        if (mPreferences == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return mPreferences;
    }

    public void setLastUpdated(ResourceType type, DateTime dateTime) {
        setLastUpdated(type, null, dateTime);
    }

    public void setLastUpdated(ResourceType type, String salt, DateTime dateTime) {
        isNull(type, "ResourceType object must not be null");
        isNull(dateTime, "DateTime object must not be null");

        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        putString(identifier, dateTime.toString());
    }



    public DateTime getLastUpdated(ResourceType type) {
        return getLastUpdated(type, null);
    }

    /**
     *
     * @param type
     * @param salt add some extra salt for a specific resource. For example an UID
     * @return
     */
    public DateTime getLastUpdated(ResourceType type, String salt) {
        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        String dateTimeString = getString(identifier);
        if (dateTimeString != null) {
            return DateTime.parse(dateTimeString);
        }
        return null;
    }

    /**
     * Removes all key-value pairs.
     */
    public void delete() {
        mPrefs.edit().clear().commit();
    }

    public void deleteLastUpdated(ResourceType type) {
        deleteLastUpdated(type, null);
    }

    public void deleteLastUpdated(ResourceType type, String salt) {
        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        deleteString(identifier);
    }

    public boolean isLastUpdatedSet(ResourceType type) {
        return getLastUpdated(type) != null;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }

    private void deleteString(String key) {
        mPrefs.edit().remove(key).commit();
    }

}