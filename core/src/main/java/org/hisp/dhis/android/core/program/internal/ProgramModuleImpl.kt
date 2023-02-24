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
package org.hisp.dhis.android.core.program.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.program.*
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListCollectionRepository

@Reusable
@Suppress("LongParameterList", "TooManyFunctions")
internal class ProgramModuleImpl @Inject constructor(
    private val programs: ProgramCollectionRepository,
    private val programIndicators: ProgramIndicatorCollectionRepository,
    private val programRules: ProgramRuleCollectionRepository,
    private val programRuleActions: ProgramRuleActionCollectionRepository,
    private val programRuleVariables: ProgramRuleVariableCollectionRepository,
    private val programSections: ProgramSectionCollectionRepository,
    private val programStages: ProgramStageCollectionRepository,
    private val programStageSections: ProgramStageSectionsCollectionRepository,
    private val programStageDataElements: ProgramStageDataElementCollectionRepository,
    private val programTrackedEntityAttributes: ProgramTrackedEntityAttributeCollectionRepository,
    private val programIndicatorEngine: ProgramIndicatorEngine,
    private val programStageWorkingLists: ProgramStageWorkingListCollectionRepository
) : ProgramModule {
    override fun programs(): ProgramCollectionRepository {
        return programs
    }

    override fun programIndicators(): ProgramIndicatorCollectionRepository {
        return programIndicators
    }

    override fun programRules(): ProgramRuleCollectionRepository {
        return programRules
    }

    override fun programRuleActions(): ProgramRuleActionCollectionRepository {
        return programRuleActions
    }

    override fun programRuleVariables(): ProgramRuleVariableCollectionRepository {
        return programRuleVariables
    }

    override fun programSections(): ProgramSectionCollectionRepository {
        return programSections
    }

    override fun programStages(): ProgramStageCollectionRepository {
        return programStages
    }

    override fun programStageSections(): ProgramStageSectionsCollectionRepository {
        return programStageSections
    }

    override fun programStageDataElements(): ProgramStageDataElementCollectionRepository {
        return programStageDataElements
    }

    override fun programTrackedEntityAttributes(): ProgramTrackedEntityAttributeCollectionRepository {
        return programTrackedEntityAttributes
    }

    override fun programIndicatorEngine(): ProgramIndicatorEngine {
        return programIndicatorEngine
    }

    override fun programStageWorkingLists(): ProgramStageWorkingListCollectionRepository {
        return programStageWorkingLists
    }
}
