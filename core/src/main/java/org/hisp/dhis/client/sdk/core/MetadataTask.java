package org.hisp.dhis.client.sdk.core;

import org.hisp.dhis.client.sdk.core.option.OptionSetInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramInteractor;
import org.hisp.dhis.client.sdk.core.program.ProgramStore.ProgramColumns;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInteractor;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.Payload;
import org.hisp.dhis.client.sdk.models.option.OptionSet;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
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
    private final ProgramInteractor programInteractor;
    private final UserInteractor userInteractor;
    private final OptionSetInteractor optionSetInteractor;
    private final TrackedEntityInteractor trackedEntityInteractor;

    private final int REQUEST_SPLIT_THRESHOLD = 64;

    static final String IDENTIFIABLE_PROPERTIES =
            "id,name,displayName,created,lastUpdated,code";

    static final String NAMEABLE_PROPERTIES =
            "shortName,displayShortName,description,displayDescription";

    public MetadataTask(UserInteractor userInteractor,
                        ProgramInteractor programInteractor,
                        OptionSetInteractor optionSetInteractor,
                        TrackedEntityInteractor trackedEntityInteractor) {
        this.userInteractor = userInteractor;
        this.optionSetInteractor = optionSetInteractor;
        this.programInteractor = programInteractor;
        this.trackedEntityInteractor = trackedEntityInteractor;
    }

    public void sync() throws IOException {
        synchronized (this) {

            User user = getCurrentUser().execute().body();

            Map<String, Program> programMap = new HashMap<>();
            Map<String, OptionSet> optionSetMap = new HashMap<>();

            //---------------------------------------------------------
            // DOWNLOADING ID AND VERSIONS FOR PROGRAMS AND OPTION SETS
            // --------------------------------------------------------

            for (UserRole userRole : user.getUserCredentials().getUserRoles()) {
                for (Program program : userRole.getPrograms()) {
                    if (program != null) {
                        programMap.put(program.getUid(), program);
                        for (ProgramStage programStage : program.getProgramStages()) {
                            for (ProgramStageDataElement programStageDataElement : programStage.getProgramStageDataElements()) {
                                if (programStageDataElement.getDataElement().getOptionSet() != null) {
                                    OptionSet optionSet = programStageDataElement.getDataElement().getOptionSet();
                                    optionSetMap.put(optionSet.getUid(), optionSet);
                                }
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------------------------------
            // FILTERING OUT WHICH PROGRAMS TO DOWNLOAD BASED ON VERSION AND EXISTENCE IN DB
            // -----------------------------------------------------------------------------

            Map<String, Program> persistedPrograms = toMap(programInteractor.store().queryAll());
            System.out.println("Persisted programs: " + persistedPrograms.values().toString());
            List<String> programsToDownload = new ArrayList<>();
            for (Program program : programMap.values()) {
                if (persistedPrograms.containsKey(program.getUid())) {
                    Program persistedProgram = persistedPrograms.get(program.getUid());
                    if (program.getVersion() > persistedProgram.getVersion()) {
                        // if program version from api is higher than in local db, download it
                        programsToDownload.add(program.getUid());
                    }
                } else {
                    // if program doesn't exist in db, download it
                    programsToDownload.add(program.getUid());
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
                if (persistedOptionSets.containsKey(optionSet.getUid())) {
                    OptionSet persistedOptionSet = persistedOptionSets.get(optionSet.getUid());
                    if (optionSet.getVersion() > persistedOptionSet.getVersion()) {
                        // if optionSet version from api is higher than in local db, download it
                        optionSetsToDownload.add(optionSet.getUid());
                    }
                } else {
                    // if optionSet doesn't exist in db, download it
                    optionSetsToDownload.add(optionSet.getUid());
                }
            }
            System.out.println("OptionSetUids to download: " + optionSetsToDownload.toString());

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

            HashSet<String> trackedEntityUidHashSet = new HashSet<>();
            System.out.println("TrackedEntityUids: " + trackedEntityUidHashSet.toString());
            List<TrackedEntity> trackedEntities = new ArrayList<>();
            if (trackedEntityUidHashSet.size() > REQUEST_SPLIT_THRESHOLD) {
                List<TrackedEntity> trackedEntityCache = new ArrayList<>();
                List<List<String>> listOfListsOfTrackedEntityUids =
                        slice(getKeysFromHashSet(trackedEntityUidHashSet), REQUEST_SPLIT_THRESHOLD);
                for (List<String> listOfSlicedTrackedEntityUids : listOfListsOfTrackedEntityUids) {
                    Payload<TrackedEntity> trackedEntitiesFromApi = getTrackedEntities(listOfSlicedTrackedEntityUids).execute().body();
                    trackedEntityCache.addAll(trackedEntitiesFromApi.items());
                }
                trackedEntities.addAll(trackedEntityCache);
            } else {
                trackedEntities = getTrackedEntities(trackedEntityUidHashSet).execute().body().items();
            }

            // ------------------------
            // END TRACKED ENTITY DOWNLOAD
            // ------------------------

            // --------------------
            // FLUSHING TO DATABASE
            // --------------------


            programInteractor.store().save(programs);
            optionSetInteractor.store().save(optionSets);
            trackedEntityInteractor.store().save(trackedEntities);
        }
    }

    private Call<User> getCurrentUser() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fields", "userCredentials[username,userRoles[programs[id,version,programStages[id,programStageSections[id],programStageDataElements[dataElement[id,optionSet[id,version]]]]]," +
                "dataSets[id]]," +
                "organisationUnits[id,name,displayName,code,lastUpdated,level,created,shortName," +
                "displayShortName,path,openingDate,closedDate,parent[id],programs[id,version]");
        return userInteractor.api().me(queryMap);
    }

    private Call<Payload<Program>> getPrograms(Collection<String> programUids) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("fields", IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES +
                ",version,externalAccess,onlyEnrollOnce,enrollmentDateLabel,displayIncidentDate," +
                "incidentDateLabel,registration,selectEnrollmentDatesInFuture,dataEntryMethod," +
                "singleEvent,ignoreOverdueEvents,relationshipFromA,selectIncidentDatesInFuture," +
                "captureCoordinates,useFirstStageDuringRegistration,displayFrontPageList," +
                "programType,relationshipType,relationshipText,trackedEntity[id]," +
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
                "programStageSections[" + IDENTIFIABLE_PROPERTIES + ",sortOrder]," +
                "programStageDataElements[" + IDENTIFIABLE_PROPERTIES + ",displayInReports," +
                "compulsory,allowProvidedElsewhere,sortOrder,allowFutureDate," +
                "dataElement[" + IDENTIFIABLE_PROPERTIES + "," + NAMEABLE_PROPERTIES +
                ",valueType,formName,displayFormName,zeroIsSignificant," +
                "optionSet[id]" +
                "]" + //end dataElement
                "]" + //end programStageDataElement
                "]"); // end programStages

        queryMap.put("filter", "id:in:" + ids(programUids));

        return programInteractor.api().list(queryMap);
    }

    private Call<Payload<OptionSet>> getOptionSets(Collection<String> optionSetUids) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("fields", IDENTIFIABLE_PROPERTIES +
                ",version,valueType,options[" + IDENTIFIABLE_PROPERTIES + "]");
        queryMap.put("filter", "id:in:" + ids(optionSetUids));

        return optionSetInteractor.api().list(queryMap);
    }

    private Call<Payload<TrackedEntity>> getTrackedEntities(Collection<String> trackedEntityUids) {
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
