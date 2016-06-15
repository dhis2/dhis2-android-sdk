/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers.wrappers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

/**
 * @author Simen Skogly Russnes on 20.08.15.
 * Wrapper to deserialize the contents of the api endpoint api/me/programs
 */
public class AssignedProgramsWrapper extends JsonDeserializer<List<OrganisationUnit>> {
    @Override
    public List<OrganisationUnit> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        JsonNode node = p.getCodec().readTree(p);
        JsonNode organisationUnitsNode = node.get(ApiEndpointContainer.ORGANISATIONUNITS);

        if (organisationUnitsNode == null) { /* in case there are no items */
            return organisationUnits;
        } else {
            Iterator<JsonNode> nodes = organisationUnitsNode.elements();
            while(nodes.hasNext()) {
                JsonNode indexNode = nodes.next();
                OrganisationUnit item = DhisController.getInstance().getObjectMapper().
                        readValue(indexNode.toString(), OrganisationUnit.class);
                organisationUnits.add(item);
            }
        }
        return organisationUnits;
    }

    public List<OrganisationUnit> deserialize(Response response) throws ConversionException, IOException {
        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        String responseBodyString = new StringConverter().fromBody(response.getBody(), String.class);
        JsonNode node = DhisController.getInstance().getObjectMapper().
                    readTree(responseBodyString);
        JsonNode organisationUnitsNode = node.get(ApiEndpointContainer.ORGANISATIONUNITS);

        if (organisationUnitsNode == null) { /* in case there are no items */
            return organisationUnits;
        } else {
            Iterator<JsonNode> nodes = organisationUnitsNode.elements();
            while(nodes.hasNext()) {
                JsonNode indexNode = nodes.next();
                OrganisationUnit item = DhisController.getInstance().getObjectMapper().
                        readValue(indexNode.toString(), OrganisationUnit.class);
                organisationUnits.add(item);
            }
        }
        return organisationUnits;
    }

    public static List<DbOperation> getOperations(List<OrganisationUnit> organisationUnits) {
        List<DbOperation> operations = new ArrayList<>();

        if(organisationUnits == null) { // if new organisationUnits is null, return before deleting old relationships
            return operations;
        }
        //delete all old relationships
        for(OrganisationUnitProgramRelationship oldOrganisationUnitProgramRelationship: MetaDataController.getOrganisationUnitProgramRelationships()) {
            operations.add(DbOperation.delete(oldOrganisationUnitProgramRelationship));
        }

        for (OrganisationUnit organisationUnit : organisationUnits) {
            for (Program program : organisationUnit.getPrograms()) {
                OrganisationUnitProgramRelationship orgUnitProgram =
                        new OrganisationUnitProgramRelationship();
                orgUnitProgram.setOrganisationUnitId(organisationUnit.getId());
                orgUnitProgram.setProgramId(program.getUid());
                operations.add(DbOperation.save(orgUnitProgram));
            }
            operations.add(DbOperation.save(organisationUnit));
        }
        return operations;
    }
}
