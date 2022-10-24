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

package org.hisp.dhis.android.core.analytics.aggregated

import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

sealed class MetadataItem(val id: String, val displayName: String) {
    class DataElementItem(val item: DataElement) : MetadataItem(item.uid(), item.displayName()!!)
    class DataElementOperandItem(val item: DataElementOperand, dataElementName: String, cocName: String?) :
        MetadataItem(item.uid()!!, "$dataElementName $cocName")

    class IndicatorItem(val item: Indicator) : MetadataItem(item.uid(), item.displayName()!!)
    class ProgramIndicatorItem(val item: ProgramIndicator) : MetadataItem(item.uid(), item.displayName()!!)
    class EventDataElementItem(val item: DataElement, val program: Program) :
        MetadataItem("${program.uid()}.${item.uid()}", "${program.displayName()} ${item.displayName()}")
    class EventAttributeItem(val item: TrackedEntityAttribute, val program: Program) :
        MetadataItem("${program.uid()}.${item.uid()}", "${program.displayName()} ${item.displayName()}")

    class CategoryItem(val item: Category) : MetadataItem(item.uid(), item.displayName()!!)
    class CategoryOptionItem(val item: CategoryOption) : MetadataItem(item.uid(), item.displayName()!!)

    class CategoryOptionGroupSetItem(uid: String, displayName: String) : MetadataItem(uid, displayName)

    class LegendItem(val item: Legend) : MetadataItem(item.uid(), item.displayName()!!)

    class OrganisationUnitItem(val item: OrganisationUnit) : MetadataItem(item.uid(), item.displayName()!!)
    class OrganisationUnitLevelItem(
        val item: OrganisationUnitLevel,
        val organisationUnitUids: List<String>
    ) : MetadataItem(item.uid(), item.displayName()!!)

    class OrganisationUnitGroupItem(
        val item: OrganisationUnitGroup,
        val organisationUnitUids: List<String>
    ) : MetadataItem(item.uid(), item.displayName()!!)

    class OrganisationUnitRelativeItem(
        val item: RelativeOrganisationUnit,
        val organisationUnitUids: List<String>
    ) : MetadataItem(item.name, item.name)

    class PeriodItem(val item: Period) : MetadataItem(item.periodId()!!, item.periodId()!!)
    class RelativePeriodItem(val item: RelativePeriod, val periods: List<Period>) : MetadataItem(item.name, item.name)
}

sealed class Dimension {
    object Data : Dimension()
    object Period : Dimension()
    object OrganisationUnit : Dimension()
    data class Category(val uid: String) : Dimension()
}

sealed class DimensionItem(val dimension: Dimension, val id: String) {
    sealed class DataItem(id: String) : DimensionItem(Dimension.Data, id), AbsoluteDimensionItem {
        data class DataElementItem(val uid: String) : DataItem(uid)
        data class DataElementOperandItem(val dataElement: String, val categoryOptionCombo: String) :
            DataItem("$dataElement.$categoryOptionCombo")

        data class IndicatorItem(val uid: String) : DataItem(uid)
        data class ProgramIndicatorItem(val uid: String) : DataItem(uid)

        sealed class EventDataItem(id: String) : DataItem(id) {
            data class DataElement(val program: String, val dataElement: String) :
                EventDataItem("$program.$dataElement")
            data class Attribute(val program: String, val attribute: String) : EventDataItem("$program.$attribute")
        }
    }

    sealed class PeriodItem(id: String) : DimensionItem(Dimension.Period, id) {
        data class Absolute(val periodId: String) : PeriodItem(periodId), AbsoluteDimensionItem
        data class Relative(val relative: RelativePeriod) : PeriodItem(relative.name)
    }

    sealed class OrganisationUnitItem(id: String) : DimensionItem(Dimension.OrganisationUnit, id) {
        data class Absolute(val uid: String) : OrganisationUnitItem(uid), AbsoluteDimensionItem
        data class Relative(val relative: RelativeOrganisationUnit) : OrganisationUnitItem(relative.name)
        data class Level(val uid: String) : OrganisationUnitItem(uid)
        data class Group(val uid: String) : OrganisationUnitItem(uid)
    }

    class CategoryItem(
        val uid: String,
        val categoryOption: String
    ) : DimensionItem(Dimension.Category(uid), categoryOption), AbsoluteDimensionItem
}

internal interface AbsoluteDimensionItem
