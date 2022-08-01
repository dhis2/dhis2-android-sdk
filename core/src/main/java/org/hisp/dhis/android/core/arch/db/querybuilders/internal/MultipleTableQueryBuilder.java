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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal;

import java.util.Collection;

public class MultipleTableQueryBuilder {

    private static final String NOT_NULL = " IS NOT NULL";
    private static final String SELECT = "SELECT ";
    private static final String WHERE = " WHERE ";
    private static final String FROM = " FROM ";
    private static final String UNION = " UNION ";
    private static final String END_STR = ";";

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder clause = new StringBuilder();
    private boolean isFirst = true;

    public MultipleTableQueryBuilder generateQuery(String columnName, Collection<String> tableNames) {
        for (String tableName : tableNames) {
            appendKeyValue(columnName, tableName);
        }
        clause.append(END_STR);
        return this;
    }

    private MultipleTableQueryBuilder appendKeyValue(String column, String tableName) {
        String andOpt = isFirst ? "" : UNION;
        isFirst = false;
        clause.append(andOpt).append(SELECT).append(column).append(FROM).append(tableName)
                .append(WHERE).append(column).append(NOT_NULL);
        return this;
    }

    public String build() {
        if (clause.length() == 0) {
            throw new RuntimeException("No columns added");
        } else {
            return clause.toString();
        }
    }
}
