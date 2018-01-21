/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.dataset;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.PeriodType;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class DataSet extends BaseNameableObject {
    private final static String PERIOD_TYPE = "periodType";
    private final static String CATEGORY_COMBO = "categoryCombo";
    private final static String MOBILE = "mobile";
    private final static String VERSION = "version";
    private final static String EXPIRY_DAYS = "expiryDays";
    private final static String TIMELY_DAYS = "timelyDays";
    private final static String NOTIFY_COMPLETING_USER = "notifyCompletingUser";
    private final static String OPEN_FUTURE_PERIODS = "openFuturePeriods";
    private final static String FIELD_COMBINATION_REQUIRED = "fieldCombinationRequired";
    private final static String VALID_COMPLETE_ONLY = "validCompleteOnly";
    private final static String NO_VALUE_REQUIRES_COMMENT = "noValueRequiresComment";
    private final static String SKIP_OFFLINE = "skipOffline";
    private final static String DATA_ELEMENT_DECORATION = "dataElementDecoration";
    private final static String RENDER_AS_TABS = "renderAsTabs";
    private final static String RENDER_HORIZONTALLY = "renderHorizontally";
    private final static String DATA_SET_ELEMENTS = "dataSetElements";

    public static final Field<DataSet, String> uid = Field.create(UID);
    public static final Field<DataSet, String> code = Field.create(CODE);
    public static final Field<DataSet, String> name = Field.create(NAME);
    public static final Field<DataSet, String> displayName = Field.create(DISPLAY_NAME);
    public static final Field<DataSet, String> created = Field.create(CREATED);
    public static final Field<DataSet, String> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<DataSet, String> shortName = Field.create(SHORT_NAME);
    public static final Field<DataSet, String> displayShortName = Field.create(DISPLAY_SHORT_NAME);
    public static final Field<DataSet, String> description = Field.create(DESCRIPTION);
    public static final Field<DataSet, String> displayDescription = Field.create(DISPLAY_DESCRIPTION);
    public static final Field<DataSet, Boolean> deleted = Field.create(DELETED);

    public static final Field<DataSet, PeriodType> periodType = Field.create(PERIOD_TYPE);
    public static final NestedField<DataSet, ObjectWithUid> categoryCombo = NestedField.create(CATEGORY_COMBO);
    public static final Field<DataSet, Boolean> mobile = Field.create(MOBILE);
    public static final Field<DataSet, Integer> version = Field.create(VERSION);
    public static final Field<DataSet, Integer> expiryDays = Field.create(EXPIRY_DAYS);
    public static final Field<DataSet, Integer> timelyDays = Field.create(TIMELY_DAYS);
    public static final Field<DataSet, Boolean> notifyCompletingUser = Field.create(NOTIFY_COMPLETING_USER);
    public static final Field<DataSet, Integer> openFuturePeriods = Field.create(OPEN_FUTURE_PERIODS);
    public static final Field<DataSet, Boolean> fieldCombinationRequired = Field.create(FIELD_COMBINATION_REQUIRED);
    public static final Field<DataSet, Boolean> validCompleteOnly = Field.create(VALID_COMPLETE_ONLY);
    public static final Field<DataSet, Boolean> noValueRequiresComment = Field.create(NO_VALUE_REQUIRES_COMMENT);
    public static final Field<DataSet, Boolean> skipOffline = Field.create(SKIP_OFFLINE);
    public static final Field<DataSet, Boolean> dataElementDecoration = Field.create(DATA_ELEMENT_DECORATION);
    public static final Field<DataSet, Boolean> renderAsTabs = Field.create(RENDER_AS_TABS);
    public static final Field<DataSet, Boolean> renderHorizontally = Field.create(RENDER_HORIZONTALLY);
    public static final NestedField<DataSet, DataElementUids> dataSetElements = NestedField.create(DATA_SET_ELEMENTS);

    public static final Fields<DataSet> allFields = Fields.<DataSet>builder().fields(
            uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, deleted,
            periodType, categoryCombo.with(ObjectWithUid.uid), mobile, version,
            expiryDays, timelyDays, notifyCompletingUser,
            openFuturePeriods, fieldCombinationRequired, validCompleteOnly, noValueRequiresComment,
            skipOffline, dataElementDecoration, renderAsTabs, renderHorizontally,
            dataSetElements.with(DataElementUids.allFields)).build();

    @Nullable
    @JsonProperty(PERIOD_TYPE)
    public abstract PeriodType periodType();

    @Nullable
    @JsonProperty(CATEGORY_COMBO)
    public abstract ObjectWithUid categoryCombo();

    @SuppressWarnings("PMD")
    public String categoryComboUid() {
        return categoryCombo() != null ? categoryCombo().uid() :
                CategoryComboModel.DEFAULT_UID;
    }

    @Nullable
    @JsonProperty(MOBILE)
    public abstract Boolean mobile();

    @Nullable
    @JsonProperty(VERSION)
    public abstract Integer version();

    @Nullable
    @JsonProperty(EXPIRY_DAYS)
    public abstract Integer expiryDays();

    @Nullable
    @JsonProperty(TIMELY_DAYS)
    public abstract Integer timelyDays();

    @Nullable
    @JsonProperty(NOTIFY_COMPLETING_USER)
    public abstract Boolean notifyCompletingUser();

    @Nullable
    @JsonProperty(OPEN_FUTURE_PERIODS)
    public abstract Integer openFuturePeriods();

    @Nullable
    @JsonProperty(FIELD_COMBINATION_REQUIRED)
    public abstract Boolean fieldCombinationRequired();

    @Nullable
    @JsonProperty(VALID_COMPLETE_ONLY)
    public abstract Boolean validCompleteOnly();

    @Nullable
    @JsonProperty(NO_VALUE_REQUIRES_COMMENT)
    public abstract Boolean noValueRequiresComment();

    @Nullable
    @JsonProperty(SKIP_OFFLINE)
    public abstract Boolean skipOffline();

    @Nullable
    @JsonProperty(DATA_ELEMENT_DECORATION)
    public abstract Boolean dataElementDecoration();

    @Nullable
    @JsonProperty(RENDER_AS_TABS)
    public abstract Boolean renderAsTabs();

    @Nullable
    @JsonProperty(RENDER_HORIZONTALLY)
    public abstract Boolean renderHorizontally();

    @Nullable
    @JsonProperty(DATA_SET_ELEMENTS)
    public abstract List<DataElementUids> dataSetElements();

    @JsonCreator
    public static DataSet create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CODE) String code,
            @JsonProperty(NAME) String name,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(SHORT_NAME) String shortName,
            @JsonProperty(DISPLAY_SHORT_NAME) String displayShortName,
            @JsonProperty(DESCRIPTION) String description,
            @JsonProperty(DISPLAY_DESCRIPTION) String displayDescription,
            @JsonProperty(PERIOD_TYPE) PeriodType periodType,
            @JsonProperty(CATEGORY_COMBO) ObjectWithUid categoryCombo,
            @JsonProperty(MOBILE) Boolean mobile,
            @JsonProperty(VERSION) Integer version,
            @JsonProperty(EXPIRY_DAYS) Integer expiryDays,
            @JsonProperty(TIMELY_DAYS) Integer timelyDays,
            @JsonProperty(NOTIFY_COMPLETING_USER) Boolean notifyCompletingUser,
            @JsonProperty(OPEN_FUTURE_PERIODS) Integer openFuturePeriods,
            @JsonProperty(FIELD_COMBINATION_REQUIRED) Boolean fieldCombinationRequired,
            @JsonProperty(VALID_COMPLETE_ONLY) Boolean validCompleteOnly,
            @JsonProperty(NO_VALUE_REQUIRES_COMMENT) Boolean noValueRequiresComment,
            @JsonProperty(SKIP_OFFLINE) Boolean skipOffline,
            @JsonProperty(DATA_ELEMENT_DECORATION) Boolean dataElementDecoration,
            @JsonProperty(RENDER_AS_TABS) Boolean renderAsTabs,
            @JsonProperty(RENDER_HORIZONTALLY) Boolean renderHorizontally,
            @JsonProperty(DATA_SET_ELEMENTS) List<DataElementUids> dataSetElements,
            @JsonProperty(DELETED) Boolean deleted) {

        return new AutoValue_DataSet(uid, code, name,
                displayName, created, lastUpdated, deleted,
                shortName, displayShortName, description, displayDescription,
                periodType, categoryCombo, mobile, version, expiryDays, timelyDays,
                notifyCompletingUser, openFuturePeriods, fieldCombinationRequired,
                validCompleteOnly, noValueRequiresComment, skipOffline,
                dataElementDecoration, renderAsTabs, renderHorizontally, dataSetElements);
    }
}
