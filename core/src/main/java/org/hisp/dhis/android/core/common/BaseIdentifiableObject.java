/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;

import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreBooleanColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.DateUtils;

import java.text.ParseException;
import java.util.Date;

public abstract class BaseIdentifiableObject implements IdentifiableObject, ObjectWithDeleteInterface {
    /* date format which should be used for all Date instances
    within models which extend BaseIdentifiableObject */
    public static SafeDateFormat DATE_FORMAT = DateUtils.DATE_FORMAT;
    public static SafeDateFormat SPACE_DATE_FORMAT = DateUtils.SPACE_DATE_FORMAT;

    public static final String UID = "id";
    public static final String UUID = "uid";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "displayName";
    public static final String CREATED = "created";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String DELETED = "deleted";

    @Override
    @JsonProperty(UID)
    public abstract String uid();

    @Override
    @Nullable
    public abstract String code();

    @Override
    @Nullable
    public abstract String name();

    @Override
    @Nullable
    public abstract String displayName();

    @Override
    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Override
    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Override
    @Nullable
    @ColumnAdapter(IgnoreBooleanColumnAdapter.class)
    public abstract Boolean deleted();

    public static Date parseDate(String dateStr) throws ParseException {
        return BaseIdentifiableObject.DATE_FORMAT.parse(dateStr);
    }

    public static Date parseSpaceDate(String dateStr) throws ParseException {
        return BaseIdentifiableObject.SPACE_DATE_FORMAT.parse(dateStr);
    }

    public static String dateToSpaceDateStr(Date date) {
        return BaseIdentifiableObject.SPACE_DATE_FORMAT.format(date);
    }

    public static String dateToDateStr(Date date) {
        return BaseIdentifiableObject.DATE_FORMAT.format(date);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder<T extends Builder> {

        @JsonProperty(UID)
        @JsonAlias({UUID})  // Introduced in 2.38 due to changes in userCredentials model DHIS2-12577
        public abstract T uid(String uid);

        public abstract T code(@Nullable String code);

        public abstract T name(@Nullable String name);

        public abstract T displayName(@Nullable String displayName);

        public abstract T created(@Nullable Date created);

        public T created(@NonNull String createdStr) throws ParseException {
            return created(BaseIdentifiableObject.DATE_FORMAT.parse(createdStr));
        }

        public abstract T lastUpdated(@Nullable Date lastUpdated);

        public T lastUpdated(@NonNull String lastUpdatedStr) throws ParseException {
            return lastUpdated(BaseIdentifiableObject.DATE_FORMAT.parse(lastUpdatedStr));
        }

        public abstract T deleted(@Nullable Boolean deleted);
    }
}
