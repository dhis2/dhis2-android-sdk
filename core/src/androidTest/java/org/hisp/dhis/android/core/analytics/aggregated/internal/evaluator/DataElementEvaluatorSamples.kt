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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.category.*
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType

object DataElementEvaluatorSamples {

    val generator = UidGeneratorImpl()

    val orgunitParent: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 1")
            .path("/$uid")
            .build()
    }

    val orgunitChild1: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 1")
            .parent(ObjectWithUid.create(orgunitParent.uid()))
            .path("/${orgunitParent.uid()}/$uid")
            .build()
    }

    val orgunitChild2: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 2")
            .parent(ObjectWithUid.create(orgunitParent.uid()))
            .path("/${orgunitParent.uid()}/$uid")
            .build()
    }

    val category: Category = Category.builder()
        .uid(generator.generate())
        .build()

    val categoryOption: CategoryOption = CategoryOption.builder()
        .uid(generator.generate())
        .build()

    val categoryCategoryOptionLink: CategoryCategoryOptionLink = CategoryCategoryOptionLink.builder()
        .category(category.uid())
        .categoryOption(categoryOption.uid())
        .build()

    val categoryCombo: CategoryCombo = CategoryCombo.builder()
        .uid(generator.generate())
        .build()

    val categoryOptionCombo: CategoryOptionCombo = CategoryOptionCombo.builder()
        .uid(generator.generate())
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val categoryCategoryComboLink: CategoryCategoryComboLink = CategoryCategoryComboLink.builder()
        .category(category.uid())
        .categoryCombo(categoryCombo.uid())
        .build()

    val categoryOptionComboCategoryOptionLink = CategoryOptionComboCategoryOptionLink.builder()
        .categoryOption(categoryOption.uid())
        .categoryOptionCombo(categoryOptionCombo.uid())
        .build()

    val dataElement1 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 1")
        .valueType(ValueType.NUMBER)
        .aggregationType(AggregationType.SUM.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElement2 = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 2")
        .valueType(ValueType.INTEGER)
        .aggregationType(AggregationType.AVERAGE.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val dataElementOperand = DataElementOperand.builder()
        .uid("${dataElement1.uid()}.${categoryOptionCombo.uid()}")
        .dataElement(ObjectWithUid.create(dataElement1.uid()))
        .categoryOptionCombo(ObjectWithUid.create(categoryOptionCombo.uid()))
        .build()

    val periodNov: Period = Period.builder()
        .periodId("201911")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-11-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2019-11-30T23:59:59.999"))
        .build()

    val periodDec: Period = Period.builder()
        .periodId("201912")
        .periodType(PeriodType.Monthly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-12-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2019-12-31T23:59:59.999"))
        .build()

    val periodQ4: Period = Period.builder()
        .periodId("2019Q4")
        .periodType(PeriodType.Quarterly)
        .startDate(DateUtils.DATE_FORMAT.parse("2019-10-01T00:00:00.000"))
        .endDate(DateUtils.DATE_FORMAT.parse("2021-12-31T23:59:59.999"))
        .build()
}
