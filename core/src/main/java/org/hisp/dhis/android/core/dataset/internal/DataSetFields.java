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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandFields;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.dataset.SectionFields;
import org.hisp.dhis.android.core.period.PeriodType;

public final class DataSetFields {

    public static final String PERIOD_TYPE = "periodType";
    public static final String CATEGORY_COMBO = "categoryCombo";
    public static final String MOBILE = "mobile";
    public static final String VERSION = "version";
    public static final String EXPIRY_DAYS = "expiryDays";
    public static final String TIMELY_DAYS = "timelyDays";
    public static final String NOTIFY_COMPLETING_USER = "notifyCompletingUser";
    public static final String OPEN_FUTURE_PERIODS = "openFuturePeriods";
    public static final String FIELD_COMBINATION_REQUIRED = "fieldCombinationRequired";
    public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
    public static final String NO_VALUE_REQUIRES_COMMENT = "noValueRequiresComment";
    public static final String SKIP_OFFLINE = "skipOffline";
    public static final String DATA_ELEMENT_DECORATION = "dataElementDecoration";
    public static final String RENDER_AS_TABS = "renderAsTabs";
    public static final String RENDER_HORIZONTALLY = "renderHorizontally";
    public static final String WORKFLOW = "workflow";
    public static final String DATA_SET_ELEMENTS = "dataSetElements";
    public static final String INDICATORS = "indicators";
    public static final String SECTIONS = "sections";
    public static final String COMPULSORY_DATA_ELEMENT_OPERANDS = "compulsoryDataElementOperands";
    public static final String DATA_INPUT_PERIODS = "dataInputPeriods";
    public static final String ACCESS = "access";
    public static final String STYLE = "style";

    public static final String ACCESS_DATA_WRITE = "accessDataWrite";

    private static FieldsHelper<DataSet> fh = new FieldsHelper<>();

    public static final Field<DataSet, String> uid = fh.uid();

    static final Fields<DataSet> allFields = Fields.<DataSet>builder()
            .fields(fh.getNameableFields())
            .fields(
                    fh.<PeriodType>field(PERIOD_TYPE),
                    fh.nestedFieldWithUid(CATEGORY_COMBO),
                    fh.<Boolean>field(MOBILE),
                    fh.<Integer>field(VERSION),
                    fh.<Integer>field(EXPIRY_DAYS),
                    fh.<Integer>field(TIMELY_DAYS),
                    fh.<Boolean>field(NOTIFY_COMPLETING_USER),
                    fh.<Integer>field(OPEN_FUTURE_PERIODS),
                    fh.<Boolean>field(FIELD_COMBINATION_REQUIRED),
                    fh.<Boolean>field(VALID_COMPLETE_ONLY),
                    fh.<Boolean>field(NO_VALUE_REQUIRES_COMMENT),
                    fh.<Boolean>field(SKIP_OFFLINE),
                    fh.<Boolean>field(DATA_ELEMENT_DECORATION),
                    fh.<Boolean>field(RENDER_AS_TABS),
                    fh.<Boolean>field(RENDER_HORIZONTALLY),
                    fh.nestedFieldWithUid(WORKFLOW),
                    fh.<DataSetElement>nestedField(DATA_SET_ELEMENTS).with(DataSetElementFields.allFields),
                    fh.nestedFieldWithUid(INDICATORS),
                    fh.<Section>nestedField(SECTIONS).with(SectionFields.allFields),

                    fh.<DataElementOperand>nestedField(COMPULSORY_DATA_ELEMENT_OPERANDS)
                            .with(DataElementOperandFields.allFields),
                    fh.<DataInputPeriod>nestedField(DATA_INPUT_PERIODS).with(DataInputPeriodFields.allFields),
                    fh.<Access>nestedField(ACCESS).with(Access.data.with(DataAccess.write)),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields)

    ).build();

    private DataSetFields() {}

}