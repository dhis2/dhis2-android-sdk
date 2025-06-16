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
package org.hisp.dhis.android.core.maintenance

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByParentUid
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStoreImpl
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.internal.ProgramRuleStoreImpl
import org.hisp.dhis.android.core.program.internal.ProgramStoreImpl

class PerformanceHintsService internal constructor(
    private val organisationUnitStore: IdentifiableObjectStore<OrganisationUnit>,
    private val programStore: IdentifiableObjectStore<Program>,
    private val programRuleStore: IdentifiableObjectStore<ProgramRule>,
    private val organisationUnitThreshold: Int,
    private val programRulesPerProgramThreshold: Int,
) {
    fun areThereExcessiveOrganisationUnits(): Boolean {
        return runBlocking { organisationUnitStore.count() > organisationUnitThreshold }
    }

    val programsWithExcessiveProgramRules: List<Program?>
        get() {
            return runBlocking {
                val programRules = programRuleStore.selectAll()

                val rulesMap: Map<String, List<ProgramRule>> =
                    mapByParentUid(programRules) { programRule -> programRule.program()!!.uid() }

                val programsWithExcessiveProgramRules: MutableList<Program?> = ArrayList()
                for ((key, value) in rulesMap) {
                    if (value.size > programRulesPerProgramThreshold) {
                        val program = programStore.selectByUid(key)
                        programsWithExcessiveProgramRules.add(program)
                    }
                }

                programsWithExcessiveProgramRules
            }
        }

    fun areThereProgramsWithExcessiveProgramRules(): Boolean {
        return programsWithExcessiveProgramRules.isNotEmpty()
    }

    fun areThereVulnerabilities(): Boolean {
        return this.areThereExcessiveOrganisationUnits() || areThereProgramsWithExcessiveProgramRules()
    }

    companion object {
        operator fun invoke(
            databaseAdapter: DatabaseAdapter,
            organisationUnitThreshold: Int,
            programRulesPerProgramThreshold: Int,
        ): PerformanceHintsService {
            return PerformanceHintsService(
                OrganisationUnitStoreImpl(databaseAdapter),
                ProgramStoreImpl(databaseAdapter),
                ProgramRuleStoreImpl(databaseAdapter),
                organisationUnitThreshold,
                programRulesPerProgramThreshold,
            )
        }
    }
}
