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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramModule;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorEngineEntityDIModule;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;

@Module(includes = {
        AnalyticsPeriodBoundaryEntityDIModule.class,
        ProgramEntityDIModule.class,
        ProgramIndicatorEngineEntityDIModule.class,
        ProgramIndicatorEntityDIModule.class,
        ProgramIndicatorLegendSetEntityDIModule.class,
        ProgramRuleActionEntityDIModule.class,
        ProgramRuleEntityDIModule.class,
        ProgramRuleVariableEntityDIModule.class,
        ProgramSectionEntityDIModule.class,
        ProgramStageDataElementEntityDIModule.class,
        ProgramStageAttributeValueEntityDIModule.class,
        ProgramStageSectionDataElementEntityDIModule.class,
        ProgramSectionAttributeEntityDIModule.class,
        ProgramStageSectionEntityDIModule.class,
        ProgramStageSectionProgramIndicatorEntityDIModule.class,
        ProgramStageEntityDIModule.class,
        ProgramTrackedEntityAttributeEntityDIModule.class,
        ProgramAttributeValueEntityDIModule.class
})
public final class ProgramPackageDIModule {

    @Provides
    @Reusable
    UidsCall<Program> programCall(ProgramCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCall<ProgramIndicator> programIndicatorCall(ProgramIndicatorCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCall<ProgramRule> programRuleCall(ProgramRuleCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCall<ProgramStage> programStageCall(ProgramStageCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    ProgramRuleService programRuleService(Retrofit retrofit) {
        return retrofit.create(ProgramRuleService.class);
    }

    @Provides
    @Reusable
    ProgramIndicatorService programIndicatorService(Retrofit retrofit) {
        return retrofit.create(ProgramIndicatorService.class);
    }

    @Provides
    @Reusable
    ProgramService programService(Retrofit retrofit) {
        return retrofit.create(ProgramService.class);
    }

    @Provides
    @Reusable
    ProgramStageService programStageService(Retrofit retrofit) {
        return retrofit.create(ProgramStageService.class);
    }

    @Provides
    @Reusable
    ProgramModule module(ProgramModuleImpl impl) {
        return impl;
    }
}