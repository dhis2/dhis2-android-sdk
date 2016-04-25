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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.persistence.models.Header;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to support loading of TrackedEntityInstances due to the strange structure of
 * the JSON output from the server.
 * Used for DHIS version < 2.20
 *
 * @author Simen Skogly Russnes on 03.03.15.
 */
public class TrackedEntityInstancesWrapper {

    /**
     * returns an Object[2] with a list of TrackedEntityInstances in [0], and TrackedEntityAttributeValues
     * in [1]
     *
     * @param body
     * @return
     */
    public static List<TrackedEntityInstance> parseTrackedEntityInstances(byte[] body) throws IOException {

        JsonNode node = DhisController.getInstance().getObjectMapper().
                readTree(body);
        JsonNode headersNode = node.get("headers");
        TypeReference<List<Header>> typeRef =
                new TypeReference<List<Header>>() {
                };
        List<Header> headers = DhisController.getInstance().getObjectMapper().
                readValue(headersNode.traverse(), typeRef);

        JsonNode rowsNode = node.get("rows");

        TypeReference<List<List<String>>> typeRefRows = new TypeReference<List<List<String>>>() {
        };

        List<List<String>> rows = DhisController.getInstance().getObjectMapper().readValue(rowsNode.traverse(), typeRefRows);
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();


        for (List<String> row : rows) {
                /*from 0-4 is hardcoded: instance, created, lastupdated, ou, te (trackedEntity),
                    * everything >4 is attribute*/
            TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
            List<TrackedEntityAttributeValue> trackedEntityAttributeValues = new ArrayList<>();
            trackedEntityInstance.setTrackedEntityInstance(row.get(0));
            trackedEntityInstance.setCreated(/*DateTime.parse(row.get(1))*/row.get(1));
            trackedEntityInstance.setLastUpdated(/*DateTime.parse(row.get(2))*/row.get(2));
            trackedEntityInstance.setOrgUnit(row.get(3));
            trackedEntityInstance.setTrackedEntity(row.get(4));
            trackedEntityInstances.add(trackedEntityInstance);
            if (row.size() <= 4) continue;
            for (int i = 5; i < row.size(); i++) {
                TrackedEntityAttributeValue value = new TrackedEntityAttributeValue();
                value.setTrackedEntityAttributeId(headers.get(i).getName());
                value.setTrackedEntityInstanceId(trackedEntityInstance.getTrackedEntityInstance());
                value.setValue(row.get(i));
                trackedEntityAttributeValues.add(value);
            }
            trackedEntityInstance.setAttributes(trackedEntityAttributeValues);
        }
        return trackedEntityInstances;
    }

}
