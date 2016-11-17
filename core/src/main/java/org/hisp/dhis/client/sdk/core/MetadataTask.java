package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.core.commons.Payload;
import org.hisp.dhis.client.sdk.core.option.OptionSetInteractor;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractor;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.models.option.OptionSet;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.user.UserRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.core.ModelUtils.toMap;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class MetadataTask {
    private final OrganisationUnitInteractor organisationUnitInteractor;
    private final ProgramInteractor programInteractor;
    private final UserInteractor userInteractor;
    private final OptionSetInteractor optionSetInteractor;
    private final TrackedEntityInteractor trackedEntityInteractor;

    private final int REQUEST_SPLIT_THRESHOLD = 64;

    static final String IDENTIFIABLE_PROPERTIES =
            "id,name,displayName,created,lastUpdated,code";

    static final String NAMEABLE_PROPERTIES =
            "shortName,displayShortName,description,displayDescription";

    public MetadataTask(OrganisationUnitInteractor organisationUnitInteractor,
                        UserInteractor userInteractor,
                        ProgramInteractor programInteractor,
                        OptionSetInteractor optionSetInteractor,
                        TrackedEntityInteractor trackedEntityInteractor) {
        this.organisationUnitInteractor = organisationUnitInteractor;
        this.userInteractor = userInteractor;
        this.optionSetInteractor = optionSetInteractor;
        this.programInteractor = programInteractor;
        this.trackedEntityInteractor = trackedEntityInteractor;
    }

    public void sync() throws IOException {
        synchronized (this) {

            User user = getCurrentUser().execute().body();

            Map<String, OrganisationUnit> organisationUnitMap = new HashMap<>();
            Map<String, Program> programMap = new HashMap<>();
            Map<String, OptionSet> optionSetMap = new HashMap<>();
            Map<String, TrackedEntity> trackedEntityMap = new HashMap<>();

            //---------------------------------------------------------
            // DOWNLOADING ID AND VERSIONS FOR PROGRAMS AND OPTION SETS
            // --------------------------------------------------------

            if (user.organisationUnits() != null) {
                for (OrganisationUnit organisationUnit : user.organisationUnits()) {
                    organisationUnitMap.put(organisationUnit.uid(), organisationUnit);
                }
            }

            for (UserRole userRole : user.userCredentials().userRoles()) {
                for (Program program : userRole.programs()) {
                    if (program != null) {
                        programMap.put(program.uid(), program);

                        if (program.trackedEntity() != null) {
                            trackedEntityMap.put(program.trackedEntity().uid(), program.trackedEntity());
                        }

                        if(program.programTrackedEntityAttributes() != null) {
                            for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : program.programTrackedEntityAttributes()) {
                                if(programTrackedEntityAttribute.trackedEntityAttribute() != null && programTrackedEntityAttribute.trackedEntityAttribute().optionSet() != null) {
                                    optionSetMap.put(programTrackedEntityAttribute.trackedEntityAttribute().optionSet().uid(), programTrackedEntityAttribute.trackedEntityAttribute().optionSet());
                                }
                            }
                        }
                        for (ProgramStage programStage : program.programStages()) {
                            for (ProgramStageDataElement programStageDataElement : programStage.programStageDataElements()) {
                                if (programStageDataElement.dataElement().optionSet() != null) {
                                    OptionSet optionSet = programStageDataElement.dataElement().optionSet();
                                    optionSetMap.put(optionSet.uid(), optionSet);
                                }
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------------------
            // FILTERING OUT WHICH ORGANISATION UNITS TO DOWNLOAD BASED ON EXISTENCE IN DB
            // -----------------------------------------------------------------------------

            Map<String, OrganisationUnit> persistedOrganisationUnits = toMap(organisationUnitInteractor.store().queryAll());
            System.out.println("Persisted orgUnits: " + persistedOrganisationUnits.values().toString());
            List<String> organisationUnitsToDownload = new ArrayList<>();
            for (OrganisationUnit organisationUnit : organisationUnitMap.values()) {
                if (!persistedOrganisationUnits.containsKey(organisationUnit.uid())) {
                    // if org unit doesn't exist in db, download it
                    organisationUnitsToDownload.add(organisationUnit.uid());
                }
            }

            System.out.println("OrgUnitUids to download: " + organisationUnitsToDownload.toString());

            System.out.println(getOrganisationUnits(organisationUnitsToDownload).request().url().toString());

            // -------------------------
            // START ORGANISATION UNIT DOWNLOADING
            // -------------------------
            List<OrganisationUnit> organisationUnits = new ArrayList<>();
            if (organisationUnitsToDownload.size() > REQUEST_SPLIT_THRESHOLD && organisationUnitsToDownload.size() > 0) {
                List<OrganisationUnit> organisationUnitCache = new ArrayList<>();
                List<List<String>> listOfListsOfOrgUnitUids =
                        slice(organisationUnitsToDownload, REQUEST_SPLIT_THRESHOLD);
                for (List<String> listOfSlicedOrgUnitUids : listOfListsOfOrgUnitUids) {
                    Payload<OrganisationUnit> orgUnitsFromApi = getOrganisationUnits(listOfSlicedOrgUnitUids).execute().body();
                    organisationUnitCache.addAll(orgUnitsFromApi.items());
                }
                organisationUnits.addAll(organisationUnitCache);
            } else {
                organisationUnits = getOrganisationUnits(organisationUnitsToDownload).execute().body().items();
            }


            // -----------------------------------------------------------------------------
            // FILTERING OUT WHICH PROGRAMS TO DOWNLOAD BASED ON VERSION AND EXISTENCE IN DB
            // -----------------------------------------------------------------------------

            Map<String, Program> persistedPrograms = toMap(programInteractor.store().queryAll());
            System.out.println("Persisted programs: (" + persistedPrograms.values().size() + ")");
            List<String> programsToDownload = new ArrayList<>();
            for (Program program : programMap.values()) {
                if (persistedPrograms.containsKey(program.uid())) {
                    Program persistedProgram = persistedPrograms.get(program.uid());
                    if (program.version() > persistedProgram.version()) {
                        // if program version from api is higher than in local db, download it
                        programsToDownload.add(program.uid());
                    }
                } else {
                    // if program doesn't exist in db, download it
                    programsToDownload.add(program.uid());
                }
            }

            System.out.println("ProgramUids to download: " + programsToDownload.toString());

            System.out.println(getPrograms(programsToDownload).request().url().toString());

            // -------------------------
            // START PROGRAM DOWNLOADING
            // -------------------------
            List<Program> programs = new ArrayList<>();
            if (programsToDownload.size() > REQUEST_SPLIT_THRESHOLD && programsToDownload.size() > 0) {
                List<Program> programsCache = new ArrayList<>();
                List<List<String>> listOfListsOfProgramUids =
                        slice(programsToDownload, REQUEST_SPLIT_THRESHOLD);
                for (List<String> listOfSlicedProgramUids : listOfListsOfProgramUids) {
                    Payload<Program> programsFromApi = getPrograms(listOfSlicedProgramUids).execute().body();
                    programsCache.addAll(programsFromApi.items());
                }
                programs.addAll(programsCache);
            } else {
                programs = getPrograms(programsToDownload).execute().body().items();
            }

            // ---------------------
            // END PROGRAMS DOWNLOAD
            // ---------------------


            // --------------------------------------------------------------------------------
            // FILTERING OUT WHICH OPTION SETS TO DOWNLOAD BASED ON VERSION AND EXISTENCE IN DB
            // --------------------------------------------------------------------------------


            Map<String, OptionSet> persistedOptionSets = toMap(optionSetInteractor.store().queryAll());
            System.out.println("Persisted optionSets: " + persistedOptionSets.values().toString());
            List<String> optionSetsToDownload = new ArrayList<>();
            for (OptionSet optionSet : optionSetMap.values()) {
                if (persistedOptionSets.containsKey(optionSet.uid())) {
                    OptionSet persistedOptionSet = persistedOptionSets.get(optionSet.uid());
                    if (optionSet.version() > persistedOptionSet.version()) {
                        // if optionSet version from api is higher than in local db, download it
                        optionSetsToDownload.add(optionSet.uid());
                    }
                } else {
                    // if optionSet doesn't exist in db, download it
                    optionSetsToDownload.add(optionSet.uid());
                }
            }
            System.out.println("OptionSetUids to download: " + optionSetsToDownload.toString());


            System.out.println(getOptionSets(optionSetsToDownload).request().url().toString());

            // --------------------------
            // START OPTION SETS DOWNLOAD
            // --------------------------

            List<OptionSet> optionSets = new ArrayList<>();
            if (optionSetsToDownload.size() > REQUEST_SPLIT_THRESHOLD && optionSetsToDownload.size() > 0) {
                List<OptionSet> optionSetCache = new ArrayList<>();
                List<List<String>> listOfListsOfOptionSetUids =
                        slice(optionSetsToDownload, REQUEST_SPLIT_THRESHOLD);
                for (List<String> listOfSlicedOptionSetUids : listOfListsOfOptionSetUids) {
                    Payload<OptionSet> optionSetsFromApi = getOptionSets(listOfSlicedOptionSetUids).execute().body();
                    optionSetCache.addAll(optionSetsFromApi.items());
                }
                optionSets.addAll(optionSetCache);
            } else {
                optionSets = getOptionSets(optionSetsToDownload).execute().body().items();
            }

            // ------------------------
            // END OPTION SETS DOWNLOAD
            // ------------------------


            // --------------------------
            // START TRACKED ENTITY DOWNLOAD
            // --------------------------


            // --------------------------------------------------------------------------------
            // FILTERING OUT WHICH TRACKED ENTITIES TO DOWNLOAD BASED EXISTENCE IN DB
            // --------------------------------------------------------------------------------


            Map<String, TrackedEntity> persistedTrackedEntities = toMap(trackedEntityInteractor.store().queryAll());
            System.out.println("Persisted trackedEntities: " + persistedTrackedEntities.values().toString());
            List<String> trackedEntitiesToDownload = new ArrayList<>();
            for (TrackedEntity trackedEntity : trackedEntityMap.values()) {
                if (!persistedTrackedEntities.containsKey(trackedEntity.uid())) {
                    // if tracked entity doesn't exist in db, download it
                    trackedEntitiesToDownload.add(trackedEntity.uid());
                }
            }
            System.out.println("TrackedEntityUids to download: " + trackedEntitiesToDownload.toString());

            System.out.println("TrackedEntityUids: " + trackedEntityMap.toString());
            List<TrackedEntity> trackedEntities = new ArrayList<>();
            if (trackedEntityMap.size() > REQUEST_SPLIT_THRESHOLD) {
                List<TrackedEntity> trackedEntityCache = new ArrayList<>();
                List<List<String>> listOfListsOfTrackedEntityUids =
                        slice(trackedEntitiesToDownload, REQUEST_SPLIT_THRESHOLD);
                for (List<String> listOfSlicedTrackedEntityUids : listOfListsOfTrackedEntityUids) {
                    Payload<TrackedEntity> trackedEntitiesFromApi = getTrackedEntities(listOfSlicedTrackedEntityUids).execute().body();
                    trackedEntityCache.addAll(trackedEntitiesFromApi.items());
                }
                trackedEntities.addAll(trackedEntityCache);
            } else {
                trackedEntities = getTrackedEntities(trackedEntitiesToDownload).execute().body().items();
            }

            // ------------------------
            // END TRACKED ENTITY DOWNLOAD
            // ------------------------

            // --------------------
            // FLUSHING TO DATABASE
            // --------------------

            if (!organisationUnits.isEmpty()) {
                organisationUnitInteractor.store().save(organisationUnits);
            }
            if (!programs.isEmpty()) {
                programInteractor.store().save(programs);
            }
            if (!optionSets.isEmpty()) {
                optionSetInteractor.store().save(optionSets);
            }
            if (!trackedEntities.isEmpty()) {
                trackedEntityInteractor.store().save(trackedEntities);
            }
        }
    }

    private Call<Payload<OrganisationUnit>> getOrganisationUnits
            (Collection<String> organisationUnitUids) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", "id,name,displayName,code,lastUpdated,level,created,shortName," +
                "displayShortName,path,openingDate,closedDate,parent[id],programs[id,version]");
        queryMap.put("filter", "id:in:" + ids(organisationUnitUids));
        return organisationUnitInteractor.api().list(queryMap);
    }

    private Call<User> getCurrentUser() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", "id,organisationUnits[id],dataViewOrganisationUnits[id]," +
                "userCredentials[" +
                "id,username,userRoles[" +
                "       id,programs[" +
                "                   id,version,trackedEntity[id],programTrackedEntityAttributes[id,trackedEntityAttribute[id,optionSet[id,version]]]," +
                "                   programStages[" +
                "                       id,programStageSections[id],programStageDataElements[" +
                "                           id,dataElement[" +
                "                               id,optionSet[id,version]" +
                "                                                   ]" +
                "                                               ]" +
                "                                           ]" +
                "                                       ]," +
                "dataSets[id]]," +
                "organisationUnits[id,name,displayName,code,lastUpdated,level,created,shortName," +
                "displayShortName,path,openingDate,closedDate,parent[id],programs[id,version]");
        queryMap.put("paging", "false");
        return userInteractor.api().me(queryMap);
    }

    //TODO: Revise if this is the best way of fetching programStageDataElements within programStageSections
    private Call<Payload<Program>> getPrograms(Collection<String> programUids) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("fields", IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES +
                ",version,externalAccess,onlyEnrollOnce,enrollmentDateLabel,displayIncidentDate," +
                "incidentDateLabel,registration,selectEnrollmentDatesInFuture,dataEntryMethod," +
                "singleEvent,ignoreOverdueEvents,relationshipFromA,selectIncidentDatesInFuture," +
                "captureCoordinates,useFirstStageDuringRegistration,displayFrontPageList," +
                "programType,relationshipType,relationshipText,trackedEntity[id]," +
                "programTrackedEntityAttributes[" +
                IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES + "," +
                "mandatory,valueType,allowFutureDate,displayInList,sortOrder," +
                "trackedEntityAttribute[" +
                IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES + ",programScope,displayInListNoProgram," +
                "pattern,sortOrderInListNoProgram,generated,displayOnVisitSchedule,valueType,orgunitScope," +
                "expression,searchScope,unique,inherit,optionSet[id]" + "]" +
                "]" +
                "programRules[" + IDENTIFIABLE_PROPERTIES + ",priority,condition," +
                "programRuleActions[id,content,location,data,programRuleActionType," +
                "programStageSection[id],dataElement[id],trackedEntityAttribute[id]," +
                "programIndicator[id],programStage[id]]" + "]," +
                "programRuleVariables[" + IDENTIFIABLE_PROPERTIES + ",useCodeForOptionSet," +
                "programRuleVariableSourceType,programStage[id],dataElement[id]" + "]," +
                "programStages[" + IDENTIFIABLE_PROPERTIES + ",executionDateLabel," +
                "allowGenerateNextVisit,validCompleteOnly,reportDateToUse,openAfterEnrollment," +
                "repeatable,captureCoordinates,formType,displayGenerateEventBox," +
                "generatedByEnrollmentDate,autoGenerateEvent,sortOrder,hideDueDate,blockEntryForm," +
                "minDaysFromStart,standardInterval," +
                "programStageSections[" + IDENTIFIABLE_PROPERTIES + ",sortOrder,programStageDataElements" + //revise from here
                "[" + IDENTIFIABLE_PROPERTIES + ",displayInReports,compulsory,allowProvidedElsewhere," +
                "sortOrder,allowFutureDate,dataElement" +
                "[" + IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES + ",valueType,formName,displayFormName,zeroIsSignificant,optionSet[id]]]]," + //Revise stop
                "programStageDataElements[" + IDENTIFIABLE_PROPERTIES + ",displayInReports," +
                "compulsory,allowProvidedElsewhere,sortOrder,allowFutureDate," +
                "dataElement[" + IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES +
                ",valueType,formName,displayFormName,zeroIsSignificant," +
                "optionSet[id]" +
                "]" + //end dataElement
                "]" + //end programStageDataElement
                "]"); // end programStages

        queryMap.put("filter", "id:in:" + ids(programUids));
        queryMap.put("paging", "false");

        return programInteractor.api().list(queryMap);
    }

    private Call<Payload<OptionSet>> getOptionSets(Collection<String> optionSetUids) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("fields", IDENTIFIABLE_PROPERTIES +
                ",version,valueType,options[" + IDENTIFIABLE_PROPERTIES + "]");
        queryMap.put("filter", "id:in:" + ids(optionSetUids));
        queryMap.put("paging", "false");

        return optionSetInteractor.api().list(queryMap);
    }

    private Call<Payload<TrackedEntity>> getTrackedEntities
            (Collection<String> trackedEntityUids) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES);
        queryMap.put("filter", "id:in:" + ids(trackedEntityUids));
        return trackedEntityInteractor.api().list(queryMap);
    }

    private static String ids(Collection<String> uids) {
        return "[" + join(uids) + "]";
    }

    public static String join(Collection<String> strings) {
        StringBuilder buffer = new StringBuilder();

        if (strings != null) {
            Iterator<? extends String> iterator = strings.iterator();

            if (iterator.hasNext()) {
                buffer.append(iterator.next());

                while (iterator.hasNext()) {
                    buffer.append(",").append(iterator.next());
                }
            }
        }

        return buffer.toString();
    }

    private List<String> getKeysFromHashSet(HashSet<String> hashSet) {
        isNull(hashSet, "HashSet must not be null");
        List<String> uniqueUids = new ArrayList<>();

        for (String uniqueUid : hashSet) {
            uniqueUids.add(uniqueUid);
        }

        return uniqueUids;
    }

    private static List<List<String>> slice(List<String> stringList, int subListSize) {
        List<List<String>> listOfSubLists = new ArrayList<>();

        if (stringList != null) {
            int leftBoundary = 0;
            int rightBoundary = subListSize < stringList.size() ? subListSize : stringList.size();

            do {
                listOfSubLists.add(stringList.subList(leftBoundary, rightBoundary));

                leftBoundary = rightBoundary;
                rightBoundary = rightBoundary + subListSize < stringList.size() ?
                        rightBoundary + subListSize : stringList.size();
            } while (leftBoundary != rightBoundary);

            return listOfSubLists;
        }

        return listOfSubLists;
    }
}
