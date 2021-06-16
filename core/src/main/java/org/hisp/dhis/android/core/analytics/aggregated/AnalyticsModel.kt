/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.analytics.aggregated

import java.util.*
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.PeriodType

sealed class MetadataItem(val id: String, val displayName: String) {
    class DataElement(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class Indicator(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class ProgramIndicator(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class Category(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class CategoryOption(uid: String, displayName: String, val category: String) : MetadataItem(uid, displayName)
    class CategoryOptionGroupSet(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class CategoryOptionGroup(
        uid: String,
        displayName: String,
        val categoryOptionGroupSet: String
    ) : MetadataItem(uid, displayName)
    class OrganisationUnit(uid: String, displayName: String) : MetadataItem(uid, displayName)
    class Period(
        periodId: String,
        val periodType: PeriodType,
        val startData: Date,
        val endDate: Date
    ) : MetadataItem(periodId, periodId)
}

sealed class Dimension {
    object Data : Dimension()
    object Period : Dimension()
    object OrganisationUnit : Dimension()
    class Category(val uid: String) : Dimension()
    class CategoryOptionGroupSet(val uid: String) : Dimension()
}

sealed class DimensionItem(val dimension: Dimension) {
    sealed class DataItem : DimensionItem(Dimension.Data) {
        data class DataElement(val uid: String) : DataItem()
        data class DataElementOperand(val uid: String, val categoryOptionCombo: String) : DataItem()
        data class Indicator(val uid: String) : DataItem()
        data class ProgramIndicator(val uid: String) : DataItem()
    }

    sealed class PeriodItem : DimensionItem(Dimension.Period) {
        data class Absolute(val periodId: String) : PeriodItem()
        data class Relative(val relative: RelativePeriod) : PeriodItem()
    }

    sealed class OrganisationUnitItem : DimensionItem(Dimension.OrganisationUnit) {
        data class Absolute(val uids: List<String>) : OrganisationUnitItem()
        data class Relative(val relative: RelativeOrganisationUnit) : OrganisationUnitItem()
        data class Level(val uid: String) : OrganisationUnitItem()
        data class Group(val uid: String) : OrganisationUnitItem()
    }

    class CategoryItem(val uid: String, val categoryOptions: List<String>) : DimensionItem(Dimension.Category(uid))

    class CategoryOptionGroupSetItem(
        val uid: String,
        val categoryOptionGroups: List<String>
    ) : DimensionItem(Dimension.CategoryOptionGroupSet(uid))
}
