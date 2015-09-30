/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.core.controllers.common.ResourceController;
import org.hisp.dhis.android.sdk.core.controllers.common.StringConverter;
import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.core.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.core.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.core.providers.ObjectMapperProvider;
import org.hisp.dhis.android.sdk.models.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.sdk.models.program.IProgramStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

import static org.hisp.dhis.android.sdk.models.common.BaseIdentifiableObject.toMap;

public final class AssignedProgramsController extends ResourceController<Program> {

    private final static String ORGANISATIONUNITS = "organisationUnits";
    private final static String PROGRAMS = "programs";
    private final static String ID = "id";

    private final IDhisApi mDhisApi;
    private final IProgramController programController;
    private final IOrganisationUnitController organisationUnitController;

    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;


    public AssignedProgramsController(IDhisApi dhisApi, IProgramController programController,
                                      IOrganisationUnitController organisationUnitController,
                                      IOrganisationUnitStore organisationUnitStore, IProgramStore programStore) {
        mDhisApi = dhisApi;
        this.programController = programController;
        this.organisationUnitController = organisationUnitController;
        this.organisationUnitStore = organisationUnitStore;
        this.programStore = programStore;
    }

    private void getAssignedProgramsDataFromServer() throws APIException {
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.ASSIGNEDPROGRAMS);
        Response response = mDhisApi.getAssignedPrograms(getAllFieldsQueryMap(lastUpdated));

        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        Map<OrganisationUnit, List<String>> organisationUnitPrograms = new HashMap<>();
        Map<String, List<String>> programOrganisationUnits = new HashMap<>();
        String responseBodyString = "";
        try {
            responseBodyString = new StringConverter().fromBody(response.getBody(), String.class);
        } catch (ConversionException e) {
            e.printStackTrace();
            return; //todo: handle
        }

        JsonNode node;
        try {
            node = ObjectMapperProvider.getInstance().
                    readTree(responseBodyString);
        } catch (IOException e) {
            node = null;
        }

        //manual deserialization of the /me/programs node for organisationUnits with programAssignments
        if (node != null) {
            JsonNode organisationUnitsNode = node.get(ORGANISATIONUNITS);
            if (organisationUnitsNode != null) { //may be null if there are no org units.
                Iterator<JsonNode> nodes = organisationUnitsNode.elements();
                List<String> programsToLoad = new ArrayList<>();
                while (nodes.hasNext()) {
                    JsonNode organisationUnitNode = nodes.next();
                    OrganisationUnit item;
                    try {
                        item = ObjectMapperProvider.getInstance().
                                readValue(organisationUnitNode.toString(), OrganisationUnit.class);
                    } catch (IOException e) {
                        item = null;
                    }
                    if (item != null) {
                        organisationUnits.add(item);
                        organisationUnitPrograms.put(item, new ArrayList<String>());
                        JsonNode programsNode = organisationUnitNode.get(PROGRAMS);

                        //Getting the program assignments for the current organisation unit
                        if (programsNode != null) {
                            Iterator<JsonNode> programsNodeIterator = programsNode.elements();
                            while (programsNodeIterator.hasNext()) {
                                JsonNode programNode = programsNodeIterator.next();
                                String programUid = programNode.get(ID).textValue();
                                List<String> organisationUnitsForProgram = programOrganisationUnits.get(programUid);
                                if (organisationUnitsForProgram == null) {
                                    organisationUnitsForProgram = new ArrayList<>();
                                    programOrganisationUnits.put(programUid, organisationUnitsForProgram);
                                }
                                organisationUnitsForProgram.add(item.getUId());

                                if (!programsToLoad.contains(programUid)) {
                                    programsToLoad.add(programUid);
                                }
                            }
                        }
                    }
                }
                //Load the programs and organisation units from server with full data
                programController.sync(programsToLoad);
                organisationUnitController.sync(organisationUnits);
            }
        }
        organisationUnits = organisationUnitStore.queryAll();
        List<Program> programs = programStore.queryAll();
        Map<String, OrganisationUnit> organisationUnitMap = toMap(organisationUnits);
        for (Program program : programs) {
            List<OrganisationUnit> organisationUnitsAssigned = new ArrayList<>();
            for (String organisationUnitId : programOrganisationUnits.get(program.getUId())) {
                organisationUnitsAssigned.add(organisationUnitMap.get(organisationUnitId));
            }
            programStore.assign(program, organisationUnitsAssigned);
        }

        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.ASSIGNEDPROGRAMS, serverTime);
    }

    @Override
    public void sync() throws APIException {
        getAssignedProgramsDataFromServer();
    }
}