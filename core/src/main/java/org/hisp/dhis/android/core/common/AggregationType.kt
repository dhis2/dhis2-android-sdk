/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.common

import org.hisp.dhis.android.core.util.SqlAggregator

enum class AggregationType(val sql: String?) {
    SUM(SqlAggregator.SUM),
    AVERAGE(SqlAggregator.AVG),
    AVERAGE_SUM_ORG_UNIT(SqlAggregator.AVG),
    LAST(null),
    LAST_AVERAGE_ORG_UNIT(null),
    LAST_LAST_ORG_UNIT(null),
    LAST_IN_PERIOD(null),
    LAST_IN_PERIOD_AVERAGE_ORG_UNIT(null),
    FIRST(null),
    FIRST_AVERAGE_ORG_UNIT(null),
    FIRST_FIRST_ORG_UNIT(null),
    COUNT(SqlAggregator.COUNT),
    STDDEV(null),
    VARIANCE(null),
    MIN(SqlAggregator.MIN),
    MAX(SqlAggregator.MAX),
    MIN_SUM_ORG_UNIT(null),
    MAX_SUM_ORG_UNIT(null),
    NONE(null),
    CUSTOM(null),
    DEFAULT(null),
}
