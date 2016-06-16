/*
 *  Copyright (c) 2016, University of Oslo
 *
 *  All rights reserved.
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

package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.android.event.EventInteractor;
import org.hisp.dhis.client.sdk.android.organisationunit.UserOrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageInteractor;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionInteractor;
import org.hisp.dhis.client.sdk.android.program.UserProgramInteractor;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class SyncWrapper {

    // metadata
    private final UserOrganisationUnitInteractor userOrganisationUnitInteractor;
    private final UserProgramInteractor userProgramInteractor;
    private final ProgramStageInteractor programStageInteractor;
    private final ProgramStageSectionInteractor programStageSectionInteractor;
    private final ProgramStageDataElementInteractor programStageDataElementInteractor;

    // program rules
    private final ProgramRuleInteractor programRuleInteractor;
    private final ProgramRuleActionInteractor programRuleActionInteractor;
    private final ProgramRuleVariableInteractor programRuleVariableInteractor;

    // data
    private final EventInteractor eventInteractor;

    public SyncWrapper(UserOrganisationUnitInteractor userOrganisationUnitInteractor,
                       UserProgramInteractor userProgramInteractor,
                       ProgramStageInteractor programStageInteractor,
                       ProgramStageSectionInteractor programStageSectionInteractor,
                       ProgramStageDataElementInteractor programStageDataElementInteractor,
                       ProgramRuleInteractor programRuleInteractor,
                       ProgramRuleActionInteractor programRuleActionInteractor,
                       ProgramRuleVariableInteractor programRuleVariableInteractor,
                       EventInteractor eventInteractor) {
        this.userOrganisationUnitInteractor = userOrganisationUnitInteractor;
        this.userProgramInteractor = userProgramInteractor;
        this.programStageInteractor = programStageInteractor;
        this.programStageSectionInteractor = programStageSectionInteractor;
        this.programStageDataElementInteractor = programStageDataElementInteractor;
        this.programRuleInteractor = programRuleInteractor;
        this.programRuleActionInteractor = programRuleActionInteractor;
        this.programRuleVariableInteractor = programRuleVariableInteractor;
        this.eventInteractor = eventInteractor;
    }

    public Observable<List<ProgramStageDataElement>> syncMetaData() {
        return Observable.zip(
                userOrganisationUnitInteractor.pull(),
                userProgramInteractor.pull(),
                new Func2<List<OrganisationUnit>, List<Program>, List<Program>>() {
                    @Override
                    public List<Program> call(List<OrganisationUnit> units, List<Program> programs) {
                        return programs;
                    }
                })
                .map(new Func1<List<Program>, List<ProgramStageDataElement>>() {
                    @Override
                    public List<ProgramStageDataElement> call(List<Program> programs) {
                        List<Program> programsWithoutRegistration = new ArrayList<>();

                        if (programs != null && !programs.isEmpty()) {
                            for (Program program : programs) {
                                if (ProgramType.WITHOUT_REGISTRATION
                                        .equals(program.getProgramType())) {
                                    programsWithoutRegistration.add(program);
                                }
                            }
                        }

                        List<ProgramStage> programStages =
                                loadProgramStages(programsWithoutRegistration);
                        List<ProgramStageSection> programStageSections =
                                loadProgramStageSections(programStages);
                        List<ProgramRule> programRules =
                                loadProgramRules(programsWithoutRegistration);
                        List<ProgramRuleAction> programRuleActions =
                                loadProgramRuleActions(programRules);
                        List<ProgramRuleVariable> programRuleVariables =
                                loadProgramRuleVariables(programsWithoutRegistration);

                        return loadProgramStageDataElements(programStages, programStageSections);
                    }
                });
    }

    public Observable<List<Event>> syncData() {
        return eventInteractor.list()
                .switchMap(new Func1<List<Event>, Observable<List<Event>>>() {
                    @Override
                    public Observable<List<Event>> call(List<Event> events) {
                        Set<String> uids = ModelUtils.toUidSet(events);
                        if (uids != null && !uids.isEmpty()) {
                            return eventInteractor.sync(uids);
                        }

                        return Observable.empty();
                    }
                });
    }

    public Observable<Boolean> checkIfSyncIsNeeded() {
        EnumSet<Action> updateActions = EnumSet.of(Action.TO_POST, Action.TO_UPDATE);
        return eventInteractor.listByActions(updateActions)
                .switchMap(new Func1<List<Event>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final List<Event> events) {
                        return Observable.create(new DefaultOnSubscribe<Boolean>() {
                            @Override
                            public Boolean call() {
                                return events != null && !events.isEmpty();
                            }
                        });
                    }
                });
    }

    public Observable<List<Event>> backgroundSync() {
        return syncMetaData()
                .subscribeOn(Schedulers.io())
                .switchMap(new Func1<List<ProgramStageDataElement>, Observable<List<Event>>>() {
                    @Override
                    public Observable<List<Event>> call(List<ProgramStageDataElement> programStageDataElements) {
                        if (programStageDataElements != null) {
                            return syncData();
                        }
                        return Observable.empty();
                    }
                });
    }

    private List<ProgramStage> loadProgramStages(List<Program> programs) {
        Set<String> stageUids = new HashSet<>();

        for (Program program : programs) {
            Set<String> programStageUids = ModelUtils.toUidSet(
                    program.getProgramStages());
            stageUids.addAll(programStageUids);
        }
        return programStageInteractor.pull(stageUids).toBlocking().first();
    }

    private List<ProgramStageSection> loadProgramStageSections(List<ProgramStage> stages) {
        Set<String> sectionUids = new HashSet<>();
        if (stages != null) {
            for (ProgramStage programStage : stages) {
                Set<String> stageSectionUids = ModelUtils.toUidSet(
                        programStage.getProgramStageSections());
                sectionUids.addAll(stageSectionUids);
            }
        }
        return programStageSectionInteractor.pull(sectionUids).toBlocking().first();
    }

    private List<ProgramStageDataElement> loadProgramStageDataElements(
            List<ProgramStage> stages, List<ProgramStageSection> programStageSections) {

        Set<String> dataElementUids = new HashSet<>();

        if (stages != null) {
            for (ProgramStage programStage : stages) {
                Set<String> stageDataElementUids = ModelUtils.toUidSet(
                        programStage.getProgramStageDataElements());
                dataElementUids.addAll(stageDataElementUids);
            }
        }
        if (programStageSections != null) {
            for (ProgramStageSection programStageSection : programStageSections) {
                Set<String> stageSectionElements = ModelUtils.toUidSet(
                        programStageSection.getProgramStageDataElements());
                dataElementUids.addAll(stageSectionElements);
            }
        }

        return programStageDataElementInteractor.pull(dataElementUids).toBlocking().first();
    }

    private List<ProgramRule> loadProgramRules(List<Program> programs) {
        return programRuleInteractor.pull(programs).toBlocking().first();
    }

    private List<ProgramRuleAction> loadProgramRuleActions(List<ProgramRule> programRules) {
        Set<String> programRuleActionUids = new HashSet<>();

        if (programRules != null && !programRules.isEmpty()) {
            for (ProgramRule programRule : programRules) {
                programRuleActionUids.addAll(
                        ModelUtils.toUidSet(programRule.getProgramRuleActions()));
            }
        }

        return programRuleActionInteractor.pull(programRuleActionUids).toBlocking().first();
    }

    private List<ProgramRuleVariable> loadProgramRuleVariables(List<Program> programs) {
        return programRuleVariableInteractor.pull(programs).toBlocking().first();
    }

}
