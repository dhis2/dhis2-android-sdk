/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers.wrappers;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.ApiEndpointContainer;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Access;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

/**
 * @author Ignacio Foche Pérez on 12.11.15.
 * Wrapper to deserialize the attributeValues of the api endpoint api/me/programs
 */
public class AttributeValuesWrapper extends JsonDeserializer<List<AttributeValue>> {
    @Override
    public List<AttributeValue> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        List<AttributeValue> attributeValues = new ArrayList<>();
        JsonNode node = p.getCodec().readTree(p);
        JsonNode attributeValueNode = node.get(ApiEndpointContainer.ATTRIBUTEVALUES);

        if (attributeValueNode == null) { /* in case there are no items */
            return attributeValues;
        } else {
            Iterator<JsonNode> nodes = attributeValueNode.elements();
            while(nodes.hasNext()) {
                JsonNode indexNode = nodes.next();
                //indexNode.get("attributeValue");
                AttributeValue item = DhisController.getInstance().getObjectMapper().
                        readValue(indexNode.toString(), AttributeValue.class);
                attributeValues.add(item);
            }
        }
        return attributeValues;
    }

    public List<AttributeValue> deserialize(Response response) throws ConversionException, IOException {
        List<AttributeValue> attributeValues = new ArrayList<>();
        String responseBodyString = new StringConverter().fromBody(response.getBody(), String.class);
        JsonNode node = DhisController.getInstance().getObjectMapper().
                    readTree(responseBodyString);
        Log.d(".AttributeValuesWrapper", "deserializing attributeValues");
        JsonNode rootNode = node.get(ApiEndpointContainer.PROGRAMS);
        Iterator<JsonNode> programsIterator = rootNode.elements();
        while(programsIterator.hasNext()) {
            JsonNode program = programsIterator.next();
            JsonNode programStages = program.get("programStages");
            Iterator<JsonNode> programStagesIterator = programStages.elements();
            while (programStagesIterator.hasNext()) {
                JsonNode programStage = programStagesIterator.next();
                JsonNode programStageSections = programStage.get("programStageSections");
                Iterator<JsonNode> programStageSectionsIterator = programStageSections.elements();
                while (programStageSectionsIterator.hasNext()) {
                    JsonNode programStageSection = programStageSectionsIterator.next();
                    JsonNode programStageDataElements = programStageSection.get("programStageDataElements");
                    Iterator<JsonNode> programStageDataElementsIterator = programStageDataElements.elements();
                    while (programStageDataElementsIterator.hasNext()) {
                        JsonNode programStageDataElement = programStageDataElementsIterator.next();
                        JsonNode attributeValuesNode = programStageDataElement.get("dataElement").get("attributeValues");
                        Iterator<JsonNode> attributeValuesIterator = attributeValuesNode.elements();
                        while (attributeValuesIterator.hasNext()) {
                            JsonNode attributeValueNode = attributeValuesIterator.next();
                            if (attributeValueNode == null) { /* in case there are no items */
                                return attributeValues;
                            } else {
                                AttributeValue attributeValue = DhisController.getInstance().getObjectMapper().
                                        readValue(attributeValueNode.toString(), AttributeValue.class);
                                attributeValues.add(attributeValue);
                            }
                        }
                    }
                }
            }
        }
        return attributeValues;
    }

    public static List<DbOperation> getOperations(List<AttributeValue> attributeValues) {
        List<DbOperation> operations = new ArrayList<>();

        for (AttributeValue attributeValue : attributeValues) {
            operations.add(DbOperation.save(attributeValue));
            operations.add(DbOperation.save(attributeValue.getAttribute()));
        }
        return operations;
    }
}
