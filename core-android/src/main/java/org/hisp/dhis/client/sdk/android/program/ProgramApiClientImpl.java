/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramApiClient;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramApiClientImpl implements ProgramApiClient {
    private final ProgramApiClientRetrofit programApiClientRetrofit;

    public ProgramApiClientImpl(ProgramApiClientRetrofit programApiClientRetrofit) {
        this.programApiClientRetrofit = programApiClientRetrofit;
    }

    @Override
    public List<Program> getPrograms(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        ApiResource<Program> apiResource = new ApiResource<Program>() {
            static final String IDENTIFIABLE_PROPERTIES =
                    "id,name,displayName,created,lastUpdated,access";

            @Override
            public String getResourceName() {
                return "programs";
            }

            @Override
            public String getBasicProperties() {
                return "id,version";
            }

            @Override
            public String getAllProperties() {
                return IDENTIFIABLE_PROPERTIES + ",version,programType," +
                        "organisationUnits[id],programStages[id],trackedEntity[id]," +
                        "programTrackedEntityAttributes[id]";
            }

            @Override
            public String getDescendantProperties() {
                return IDENTIFIABLE_PROPERTIES + ",version,programType,organisationUnits[id],trackedEntity[" + IDENTIFIABLE_PROPERTIES + "]," +
                        "programTrackedEntityAttributes[" + IDENTIFIABLE_PROPERTIES + ",mandatory," + // start programTrackedEntityAttributes
                        "displayShortName,externalAccess,valueType,allowFutureDate,displayInList,program[id]," +
                        "trackedEntityAttribute[" + IDENTIFIABLE_PROPERTIES + ",unique,programScope," + // start trackedEntityAttribute of parent programTrackedEntityAttributes
                        "orgunitScope,displayInListNoProgram,displayOnVisitSchedule,externalAccess," +
                        "valueType,confidential,inherit,sortOrderVisitSchedule,dimension,sortOrderInListNoProgram," +
                        "optionSet[" + IDENTIFIABLE_PROPERTIES + ",version,options[" + IDENTIFIABLE_PROPERTIES + ",code]]]]" + //end programTrackedEntityAttributes
                        ",displayFrontPageList,useFirstStageDuringRegistration," +
                        "selectEnrollmentDatesInFuture,incidentDateLabel,selectIncidentDatesInFuture," +
                        "onlyEnrollOnce,enrollmentDateLabel,ignoreOverdueEvents,displayIncidentDate," +
                        "withoutRegistration,registration,relationshipFromA," +
                        "programStages[" + IDENTIFIABLE_PROPERTIES + ",dataEntryType," + // start programStages
                        "blockEntryForm,reportDateDescription,executionDateLabel," +
                        "displayGenerateEventBox,description,externalAccess,openAfterEnrollment," +
                        "captureCoordinates,defaultTemplateMessage,remindCompleted," +
                        "validCompleteOnly,sortOrder,generatedByEnrollmentDate,preGenerateUID," +
                        "autoGenerateEvent,allowGenerateNextVisit,repeatable,minDaysFromStart," +
                        "program[id],programStageSections[" + IDENTIFIABLE_PROPERTIES + ",sortOrder," + // start programStageSections of parent programStages
                        "programStage[id],programStageDataElements[id]" + "]," +
                        "programStageDataElements[" + IDENTIFIABLE_PROPERTIES + ",programStage[id]," + // start programStageDataElements of parent programStageSections
                        "allowFutureDate,sortOrder,displayInReports,allowProvidedElsewhere," +
                        "compulsory,dataElement[" + IDENTIFIABLE_PROPERTIES + "shortName,valueType," + // start dataElement of parent programStageDataElements
                        "zeroIsSignificant,aggregationOperator,formName,numberType,domainType," +
                        "dimension,displayFormName,optionSet[" + IDENTIFIABLE_PROPERTIES + // start optionSet of parent dataElement
                        ",version,options[" + IDENTIFIABLE_PROPERTIES + ",code]]]]]"; // end
            }

            @Override
            public Call<Map<String, List<Program>>> getEntities(
                    Map<String, String> queryMap, List<String> filters) throws ApiException {
                return programApiClientRetrofit.getPrograms(queryMap, filters);
            }
        };

        List<Program> programs = getCollection(apiResource, fields, lastUpdated, uids);

        for (Program program : programs) {
            if (program.getProgramStages() != null && !program.getProgramStages().isEmpty()) {
                for (ProgramStage programStage : program.getProgramStages()) {
                    if (programStage.getProgramStageSections() != null && !programStage.getProgramStageSections().isEmpty()) {
                        for (ProgramStageSection programStageSection : programStage.getProgramStageSections()) {
                            if (programStageSection.getProgramStageDataElements() != null && !programStageSection.getProgramStageDataElements().isEmpty()) {
                                for (int i = 0; i < programStageSection.getProgramStageDataElements().size(); i++) {
                                    ProgramStageDataElement programStageDataElement = programStageSection.getProgramStageDataElements().get(i);
                                    programStageDataElement.setSortOrderWithinProgramStageSection(i);
                                }
                            }

                            if (programStage.getProgramStageDataElements() != null && !programStage.getProgramStageDataElements().isEmpty()) {
                                for (ProgramStageDataElement programStageDataElement : programStage.getProgramStageDataElements()) {
                                    if (programStageDataElement.getDataElement() != null && programStageDataElement.getDataElement().getOptionSet() != null) {
                                        OptionSet optionSet = programStageDataElement.getDataElement().getOptionSet();

                                        if (optionSet.getOptions() != null) {
                                            for (int i = 0; i < optionSet.getOptions().size(); i++) {
                                                Option option = optionSet.getOptions().get(i);
                                                option.setSortOrder(i);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return programs;
    }
}
