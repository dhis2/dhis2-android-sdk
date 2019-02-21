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

package org.hisp.dhis.android.core.utils;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;

/**
 * A collection of convenience functions/abstractions to be used by the tests.
 */
public class ColumnsArrayUtils {

    public static Object[] getModelAsObjectArray(BaseModel m) {
        return new Object[] {
                m.id()
        };
    }

    public static Object[] getIdentifiableModelAsObjectArray(BaseIdentifiableObjectModel m) {
        return Utils.appendInNewArray(getModelAsObjectArray(m),
                m.uid(), m.code(), m.name(), m.displayName(),
                m.createdStr(), m.lastUpdatedStr()
        );
    }

    public static Object[] getNameableModelAsObjectArray(BaseNameableObjectModel m) {
        return Utils.appendInNewArray(getIdentifiableModelAsObjectArray(m),
                m.shortName(), m.displayShortName(),
                m.description(), m.displayDescription()
        );
    }

    public static String[] getColumnsWithId(String[] columns) {
        return Utils.appendInNewArray(new String[] {BaseModel.Columns.ID},
                columns
        );
    }
}
