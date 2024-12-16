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
package org.hisp.dhis.android.core.attribute

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.attribute.AttributeDTO
import org.junit.Test

class AttributeShould : BaseObjectKotlinxShould("attribute/attribute.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val attribute = deserialize(AttributeDTO.serializer())

        Truth.assertThat(attribute.uid).isEqualTo("r6KOit2qCGw")
        Truth.assertThat(attribute.name).isEqualTo("Name Pattern")
        Truth.assertThat(attribute.displayName).isEqualTo("Name Pattern")
        Truth.assertThat(attribute.lastUpdated).isEqualTo("2020-07-06T06:27:19.164")
        Truth.assertThat(attribute.created).isEqualTo("2020-07-06T06:27:19.164")
        Truth.assertThat(attribute.valueType).isEqualTo("TEXT")
        Truth.assertThat(attribute.unique).isEqualTo(false)
        Truth.assertThat(attribute.mandatory).isEqualTo(false)
        Truth.assertThat(attribute.programStageAttribute).isEqualTo(true)
        Truth.assertThat(attribute.indicatorAttribute).isEqualTo(false)
        Truth.assertThat(attribute.indicatorGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.userGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.dataElementAttribute).isEqualTo(false)
        Truth.assertThat(attribute.constantAttribute).isEqualTo(false)
        Truth.assertThat(attribute.categoryOptionAttribute).isEqualTo(false)
        Truth.assertThat(attribute.optionSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.sqlViewAttribute).isEqualTo(false)
        Truth.assertThat(attribute.legendSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.trackedEntityAttributeAttribute).isEqualTo(false)
        Truth.assertThat(attribute.organisationUnitAttribute).isEqualTo(false)
        Truth.assertThat(attribute.dataSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.documentAttribute).isEqualTo(false)
        Truth.assertThat(attribute.validationRuleGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.dataElementGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.sectionAttribute).isEqualTo(false)
        Truth.assertThat(attribute.trackedEntityTypeAttribute).isEqualTo(false)
        Truth.assertThat(attribute.userAttribute).isEqualTo(false)
        Truth.assertThat(attribute.categoryOptionGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.programAttribute).isEqualTo(false)
        Truth.assertThat(attribute.categoryAttribute).isEqualTo(false)
        Truth.assertThat(attribute.categoryOptionComboAttribute).isEqualTo(false)
        Truth.assertThat(attribute.categoryOptionGroupSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.validationRuleAttribute).isEqualTo(false)
        Truth.assertThat(attribute.programIndicatorAttribute).isEqualTo(false)
        Truth.assertThat(attribute.organisationUnitGroupAttribute).isEqualTo(false)
        Truth.assertThat(attribute.dataElementGroupSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.organisationUnitGroupSetAttribute).isEqualTo(false)
        Truth.assertThat(attribute.optionAttribute).isEqualTo(false)
    }
}
