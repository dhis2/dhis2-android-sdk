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
import org.hisp.dhis.android.sdk.persistence.models.DataElementAttributeValue;
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
public class AttributeValuesWrapper {

    private static final String PAGER = "pager";
    private static final String PAGE = "page";
    private static final String PAGECOUNT = "pageCount";
    public static final String PROGRAM_STAGES = "programStages";
    public static final String PROGRAM_STAGE_SECTIONS = "programStageSections";
    public static final String PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    public static final String DATA_ELEMENT = "dataElement";
    public static final String ID = "id";
    public static final String ATTRIBUTE_VALUES = "attributeValues";

    public PaginatedListDataElementAttributeValue deserialize(Response response) throws ConversionException, IOException {
        String responseBodyString = new StringConverter().fromBody(response.getBody(), String.class);
        JsonNode node = DhisController.getInstance().getObjectMapper().
                    readTree(responseBodyString);
        Log.d(".AttributeValuesWrapper", "deserializing attributeValues");
        PaginatedListDataElementAttributeValue paginatedListDataElementAttributeValue = new PaginatedListDataElementAttributeValue();

        //Load pagination info
        paginatedListDataElementAttributeValue.setCurrentPage(node.get(PAGER).get(PAGE).asInt());
        paginatedListDataElementAttributeValue.setTotalPages(node.get(PAGER).get(PAGECOUNT).asInt());

        //Load program attributevalues info
        JsonNode rootNode = node.get(ApiEndpointContainer.PROGRAMS);
        Iterator<JsonNode> programsIterator = rootNode.elements();
        while(programsIterator.hasNext()) {
            Iterator<JsonNode> programStagesIterator = programsIterator.next().get(PROGRAM_STAGES).elements();
            while (programStagesIterator.hasNext()) {
                Iterator<JsonNode> programStageSectionsIterator = programStagesIterator.next().get(PROGRAM_STAGE_SECTIONS).elements();
                while (programStageSectionsIterator.hasNext()) {
                    Iterator<JsonNode> programStageDataElementsIterator = programStageSectionsIterator.next().get(PROGRAM_STAGE_DATA_ELEMENTS).elements();
                    while (programStageDataElementsIterator.hasNext()) {
                        JsonNode programStageDataElement = programStageDataElementsIterator.next();
                        String dataElementId = programStageDataElement.get(DATA_ELEMENT).get(ID).asText();
                        Iterator<JsonNode> attributeValuesIterator = programStageDataElement.get(DATA_ELEMENT).get(ATTRIBUTE_VALUES).elements();
                        while (attributeValuesIterator.hasNext()) {
                            JsonNode attributeValueNode = attributeValuesIterator.next();
                            if (attributeValueNode == null) { /* in case there are no items */
                                return paginatedListDataElementAttributeValue;
                            } else {
                                AttributeValue attributeValue = DhisController.getInstance().getObjectMapper().
                                        readValue(attributeValueNode.toString(), AttributeValue.class);
                                DataElementAttributeValue dataElementAttributeValue = new DataElementAttributeValue();
                                dataElementAttributeValue.setAttributeValue(attributeValue);
                                dataElementAttributeValue.setDataElementId(dataElementId);
                                paginatedListDataElementAttributeValue.dataElementAttributeValues.add(dataElementAttributeValue);
                            }
                        }
                    }
                }
            }
        }
        return paginatedListDataElementAttributeValue;
    }

    public static List<DbOperation> getOperations(List<DataElementAttributeValue> attributeValues) {
        List<DbOperation> operations = new ArrayList<>();

        for (DataElementAttributeValue attributeValue : attributeValues) {
            operations.add(DbOperation.save(attributeValue.getAttributeValue().getAttribute()));
            operations.add(DbOperation.save(attributeValue.getAttributeValue()));
            operations.add(DbOperation.save(attributeValue));
        }

        return operations;
    }

    /**
     * Wrapper that includes pagination information on top of a DataElementAttributeValue list.
     */
    public class PaginatedListDataElementAttributeValue{

        /**
         * Current retrieved page
         */
        Integer currentPage;

        /**
         * Max number of pages according to server data
         */
        Integer totalPages;

        /**
         * List of retrieved data from server
         */
        List<DataElementAttributeValue> dataElementAttributeValues;

        PaginatedListDataElementAttributeValue(){
            currentPage=0;
            totalPages=0;
            dataElementAttributeValues = new ArrayList<>();
        }

        /**
         * Tells if there are more pages to load
         * @return true|false
         */
        public boolean hasMorePages(){
            return currentPage<totalPages;
        }

        /**
         * Advance pagination params to next page
         */
        public void next(){
            currentPage++;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public List<DataElementAttributeValue> getDataElementAttributeValues(){
            return dataElementAttributeValues;
        }


    }
}
