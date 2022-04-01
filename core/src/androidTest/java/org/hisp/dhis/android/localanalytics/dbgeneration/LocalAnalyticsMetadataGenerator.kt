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
package org.hisp.dhis.android.localanalytics.dbgeneration

import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.data.category.CategoryComboSamples
import org.hisp.dhis.android.core.data.category.CategoryOptionComboSamples
import org.hisp.dhis.android.core.data.dataelement.DataElementSamples
import org.hisp.dhis.android.core.data.organisationunit.OrganisationUnitSamples
import org.hisp.dhis.android.core.data.program.ProgramSamples
import org.hisp.dhis.android.core.data.program.ProgramStageSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityAttributeSamples
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

internal class LocalAnalyticsMetadataGenerator(private val params: LocalAnalyticsMetadataParams) {

    fun getOrganisationUnits(): List<OrganisationUnit> {
        val root = OrganisationUnitSamples.getOrganisationUnit("OU", 1, null)
        val children = getOrganisationUnitChildren(root)
        val grandchildren = children.flatMap { ch -> getOrganisationUnitChildren(ch) }
        return listOf(root) + children + grandchildren
    }

    private fun getOrganisationUnitChildren(parent: OrganisationUnit): List<OrganisationUnit> {
        return (1..params.organisationUnitChildren).map { i ->
            OrganisationUnitSamples.getOrganisationUnit(
                "${parent.name()} $i", parent.level()!! + 1,
                parent
            )
        }
    }

    fun getCategoryCombos(): List<CategoryCombo> {
        val default = CategoryComboSamples.getCategoryCombo("Default", true)
        val cc2 = CategoryComboSamples.getCategoryCombo("CC2", false)
        val cc3 = CategoryComboSamples.getCategoryCombo("CC3", false)
        return listOf(default, cc2, cc3)
    }

    fun getCategoryOptionCombos(categoryCombos: List<CategoryCombo>): List<CategoryOptionCombo> {
        val coc1 = getCategoryOptionCombos(categoryCombos[0], 1)
        val coc2 = getCategoryOptionCombos(categoryCombos[1], params.categoryOptionCombos2)
        val coc3 = getCategoryOptionCombos(categoryCombos[2], params.categoryOptionCombos3)
        return coc1 + coc2 + coc3
    }

    private fun getCategoryOptionCombos(categoryCombo: CategoryCombo, count: Int): List<CategoryOptionCombo> {
        return (1..count).map { i ->
            CategoryOptionComboSamples.getCategoryOptionCombo("COC ${categoryCombo.name()} $i", categoryCombo)
        }
    }

    fun getDataElementsAggregated(categoryCombos: List<CategoryCombo>): List<DataElement> {
        return categoryCombos.flatMap { categoryCombo ->
            (1..params.dataElementsAggregated).map { i ->
                DataElementSamples.getDataElement(
                    "DE Aggr $i", null,
                    ObjectWithUid.create(categoryCombo.uid()), "AGGREGATE"
                )
            }
        }
    }

    fun getDataElementsTracker(categoryCombo: CategoryCombo): List<DataElement> {
        return (1..params.dataElementsTracker).map { i ->
            DataElementSamples.getDataElement(
                "DE Tracker $i", null,
                ObjectWithUid.create(categoryCombo.uid()), "TRACKER"
            )
        }
    }

    fun getPrograms(categoryCombo: CategoryCombo): List<Program> {
        val withReg = ProgramSamples.getProgram(
            "Program with registration",
            ProgramType.WITH_REGISTRATION, categoryCombo
        )
        val withoutReg = ProgramSamples.getProgram(
            "Program without registration",
            ProgramType.WITHOUT_REGISTRATION, categoryCombo
        )
        return listOf(withReg, withoutReg)
    }

    fun getProgramStages(programs: List<Program>): List<ProgramStage> {
        val withReg = getProgramStages(programs[0], params.programStagesWithRegistration)
        val withoutReg = getProgramStages(programs[1], params.programStagesWithoutRegistration)
        return withReg + withoutReg
    }

    private fun getProgramStages(program: Program, count: Int): List<ProgramStage> {
        return (1..count).map { i ->
            ProgramStageSamples.getProgramStage("Stage ${program.name()} $i", program)
        }
    }

    fun getTrackedEntityAttributes(): List<TrackedEntityAttribute> {
        return (1..params.trackedEntityAttributes).map { i ->
            TrackedEntityAttributeSamples.get("TEA $i")
        }
    }
}
