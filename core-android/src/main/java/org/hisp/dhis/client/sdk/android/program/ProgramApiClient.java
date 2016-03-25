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

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.IProgramApiClient;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramApiClient implements IProgramApiClient {

    private final IProgramApiClientRetrofit mIProgramApiClientRetrofit;

    public ProgramApiClient(IProgramApiClientRetrofit IProgramApiClientRetrofit) {
        this.mIProgramApiClientRetrofit = IProgramApiClientRetrofit;
    }

    public static List<Program> unwrapList(JsonNode jsonNode) {
//        TypeReference<List<Program>> typeRef = new TypeReference<List<Program>>() {
//        };
//        List<Program> programs = null;
//        try {
//            if (jsonNode.has("programs")) {
//                // programs = ObjectMapperProvider.getInstance().
//                        // readValue(jsonNode.get("programs").traverse(), typeRef);
//            } else {
//                programs = new ArrayList<>();
//            }
//        } catch (IOException e) {
//            programs = new ArrayList<>();
//            e.printStackTrace();
//        }
//        return programs;
        return null;
    }

    private static String getFieldsFilter() {
        return "lastUpdated," +
                "id,created,name,shortName,ignoreOverdueEvents,skipOffline,dataEntryMethod," +
                "enrollmentDateLabel,onlyEnrollOnce,version,selectIncidentDatesInFuture," +
                "incidentDateLabel,selectEnrollmentDatesInFuture,displayName,displayShortName," +
                "externalAccess,displayFrontPageList,programType,relationshipFromA," +
                "relationshipText" +
                "displayIncidentDate,trackedEntity[created,lastUpdated,name,id," +
                "displayDescription," +
                "externalAccess],programIndicators[lastUpdated,id,created,name,shortName," +
                "aggregationType,dimensionType,displayName,displayInForm,publicAccess," +
                "description," +
                "displayShortName,externalAccess,displayDescription,expression,decimals," +
                "program[id]]," +
                "programTrackedEntityAttributes[lastUpdated,id,created,name,shortName," +
                "displayName," +
                "mandatory,displayShortName,externalAccess,valueType,allowFutureDate," +
                "dimensionItem," +
                "displayInList,program[id],trackedEntityAttribute[lastUpdated,id,created,name," +
                "shortName,dimensionType,programScope,displayInListNoProgram,displayName," +
                "description,displayShortName,externalAccess,sortOrderInListNoProgram," +
                "displayOnVisitSchedule,valueType,sortOrderInVisitSchedule,orgunitScope," +
                "confidential,displayDescription,dimensionItem,unique,inherit,optionSetValue," +
                "optionSet[created,lastUpdated,name,id,displayName,version,externalAccess," +
                "valueType,options[code,created,lastUpdated,name,id,displayName,externalAccess" +
                "]]]],programStages[lastUpdated,id,created,name,executionDateLabel," +
                "allowGenerateNextVisit,validCompleteOnly,pregenerateUid,displayName,description," +
                "externalAccess,openAfterEnrollment,repeatable,captureCoordinates,formType," +
                "remindCompleted,displayGenerateEventBox,generatedByEnrollmentDate," +
                "defaultTemplateMessage,autoGenerateEvent,sortOrder,hideDueDate,blockEntryForm," +
                "minDaysFromStart,program[id],programStageDataElements[created,lastUpdated,id," +
                "displayInReports,externalAccess,compulsory,allowProvidedElsewhere,sortOrder," +
                "allowFutureDates,programStage[id],dataElement[code,lastUpdated,id,created,name," +
                "shortName,aggregationType,dimensionType,domainType,displayName,publicAccess," +
                "displayShortName,externalAccess,valueType,formName,dimensionItem," +
                "displayFormName," +
                "zeroIsSignificant,url,optionSetValue,optionSet[created,lastUpdated,name,id," +
                "displayName,version,externalAccess,valueType,options[code,created,lastUpdated," +
                "name,id,displayName,externalAccess]]]],programStageSections[created,lastUpdated," +
                "name,id,displayName,externalAccess,sortOrder,programStage[id]," +
                "programIndicators[id],programStageDataElements[id]]]";
    }

    // @Override
    public List<Program> getPrograms(Fields fields, DateTime dateTime) {
        switch (fields) {
            case ALL:
                return getFullPrograms(dateTime);
            case BASIC:
                return getBasicPrograms(dateTime);
            default:
                return null;
        }
    }

    // @Override
    public Program getProgram(String s, Fields fields, DateTime dateTime) {
        switch (fields) {
            case ALL:
                return getFullProgram(s, dateTime);
            case BASIC:
                return getBasicProgram(s, dateTime);
            default:
                return null;
        }
    }

    private List<Program> getFullPrograms(DateTime lastUpdated) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("fields", getFieldsFilter());
//        queryMap.put("paging", "false");
//        if (lastUpdated != null) {
//            queryMap.put("lastUpdated", lastUpdated.toString());
//        }
//        List<Program> updatedPrograms = unwrapList(call(mIProgramApiClientRetrofit.getPrograms
//                (queryMap)));
//        for (Program program : updatedPrograms) {
//            fixRelationships(program);
//        }
//        return updatedPrograms;

        return null;
    }

    private List<Program> getBasicPrograms(DateTime lastUpdated) {
//        Map<String, String> queryMap = new HashMap<>();
//        queryMap.put("fields", "id");
//        queryMap.put("paging", "false");
//        if (lastUpdated != null) {
//            queryMap.put("lastUpdated", lastUpdated.toString());
//        }
//        List<Program> updatedPrograms = unwrapList(call(mIProgramApiClientRetrofit.getPrograms
//                (queryMap)));
//        return updatedPrograms;
        return null;
    }

    public Program getFullProgram(String uid, DateTime lastUpdated) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", getFieldsFilter());
        if (lastUpdated != null) {
            queryMap.put("lastUpdated", lastUpdated.toString());
        }
//        Program program = call(mIProgramApiClientRetrofit.getProgram(uid, queryMap));
//        fixRelationships(program);
//        return program;
        return null;
    }

    public Program getBasicProgram(String uid, DateTime lastUpdated) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", "id");
        if (lastUpdated != null) {
            queryMap.put("lastUpdated", lastUpdated.toString());
        }
        // Program program = call(mIProgramApiClientRetrofit.getProgram(uid, queryMap));
        // return program;
        return null;
    }

    private void fixRelationships(Program program) {
        Map<String, ProgramIndicator> programIndicatorMap =
                ModelUtils.toMap(program.getProgramIndicators());
        Map<String, ProgramTrackedEntityAttribute> programTrackedEntityAttributeMap =
                ModelUtils.toMap(program.getProgramTrackedEntityAttributes());
        for (ProgramIndicator programIndicator : programIndicatorMap.values()) {
            programIndicator.setProgram(program);
        }
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute :
                programTrackedEntityAttributeMap.values()) {
            programTrackedEntityAttribute.setProgram(program);
            TrackedEntityAttribute trackedEntityAttribute = programTrackedEntityAttribute
                    .getTrackedEntityAttribute();
            if (trackedEntityAttribute.getOptionSet() != null) {
                OptionSet optionSet = trackedEntityAttribute.getOptionSet();
                for (Option option : optionSet.getOptions()) {
                    option.setOptionSet(optionSet);
                }
            }
        }
        if (program.getProgramStages() != null) {
            for (ProgramStage programStage : program.getProgramStages()) {
                programStage.setProgram(program);
                if (programStage.getProgramIndicators() != null) {
                    List<ProgramIndicator> fullProgramStageProgramIndicators = new ArrayList<>();
                    for (ProgramIndicator programIndicatorWithOnlyUid : programStage
                            .getProgramIndicators()) {
                        ProgramIndicator fullProgramIndicator = programIndicatorMap.get
                                (programIndicatorWithOnlyUid.getUId());
                        fullProgramIndicator.setProgramStage(programStage);
                        fullProgramStageProgramIndicators.add(fullProgramIndicator);
                    }
                    programStage.setProgramIndicators(fullProgramStageProgramIndicators);
                }

                Map<String, ProgramStageDataElement> programStageDataElementMap =
                        ModelUtils.toMap(programStage.getProgramStageDataElements());
                for (ProgramStageDataElement programStageDataElement : programStageDataElementMap
                        .values()) {
                    programStageDataElement.setProgramStage(programStage);
                    DataElement dataElement = programStageDataElement.getDataElement();
                    if (dataElement.getOptionSet() != null) {
                        OptionSet optionSet = dataElement.getOptionSet();
                        for (Option option : optionSet.getOptions()) {
                            option.setOptionSet(optionSet);
                        }
                    }
                }
                if (programStage.getProgramStageSections() != null) {
                    for (ProgramStageSection programStageSection : programStage
                            .getProgramStageSections()) {
                        programStageSection.setProgramStage(programStage);
                        List<ProgramIndicator> fullProgramStageSectionProgramIndicators = new
                                ArrayList<>();
                        for (ProgramIndicator programIndicatorWithOnlyUid : programStageSection
                                .getProgramIndicators()) {
                            ProgramIndicator fullProgramIndicator = programIndicatorMap.get
                                    (programIndicatorWithOnlyUid.getUId());
                            fullProgramIndicator.setProgramStageSection(programStageSection);
                            fullProgramStageSectionProgramIndicators.add(fullProgramIndicator);
                        }
                        programStageSection.setProgramIndicators
                                (fullProgramStageSectionProgramIndicators);

                        if (programStageSection.getProgramStageDataElements() != null) {
                            List<ProgramStageDataElement>
                                    fullProgramStageDataElementsForProgramStageSection = new
                                    ArrayList<>();
                            for (ProgramStageDataElement programStageDataElementWithOnlyUid :
                                    programStageSection.getProgramStageDataElements()) {
                                ProgramStageDataElement fullProgramStageDataElement =
                                        programStageDataElementMap.get
                                                (programStageDataElementWithOnlyUid.getUId());
                                fullProgramStageDataElement.setProgramStageSection
                                        (programStageSection);
                                fullProgramStageDataElementsForProgramStageSection.add
                                        (fullProgramStageDataElement);
                            }
                            programStageSection.setProgramStageDataElements
                                    (fullProgramStageDataElementsForProgramStageSection);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Program> getPrograms(Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return null;
    }
}
