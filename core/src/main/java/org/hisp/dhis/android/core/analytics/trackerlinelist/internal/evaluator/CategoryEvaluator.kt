/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator.TrackerLineListSQLLabel.EventAlias
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkTableInfo
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo
import org.hisp.dhis.android.core.category.CategoryTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo

internal class CategoryEvaluator(
    private val item: TrackerLineListItem.Category,
    private val metadata: Map<String, MetadataItem>,
) : TrackerLineListEvaluator() {

    override fun getSelectSQLForEvent(): String {
        return "SELECT CO.${CategoryOptionTableInfo.Columns.DISPLAY_NAME} " +
        "FROM ${CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO.name()} COCCOL " +
        "JOIN ${CategoryOptionTableInfo.TABLE_INFO.name()} CO ON COCCOL.${CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION} = " +
        "CO.${CategoryOptionTableInfo.Columns.UID} " +
        "WHERE COCCOL.${CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION_COMBO} = " +
            "$EventAlias.${EventTableInfo.Columns.ATTRIBUTE_OPTION_COMBO} " +
        "AND EXISTS ( " +
            "SELECT 1 " +
            "FROM ${CategoryCategoryOptionLinkTableInfo.TABLE_INFO.name()} CCOL " +
            "WHERE CCOL.${CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY} = '${item.uid}' " +
            "AND COCCOL.${CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION} = " +
            "CCOL.${CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION} " +
            ") "
    }

    override fun getWhereSQLForEvent(): String {
        return " 1 "
    }

    override fun getSelectSQLForEnrollment(): String {
        throw AnalyticsException.InvalidArguments("Category is not supported in ENROLLMENT output type")
    }

    override fun getWhereSQLForEnrollment(): String {
        throw AnalyticsException.InvalidArguments("Category is not supported in ENROLLMENT output type")
    }


}