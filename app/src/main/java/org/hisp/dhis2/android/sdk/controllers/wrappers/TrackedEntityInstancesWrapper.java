package org.hisp.dhis2.android.sdk.controllers.wrappers;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.persistence.models.Header;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class to support loading of TrackedEntityInstances due to the strange structure of
 * the JSON output from the server.
 * @author Simen Skogly Russnes on 03.03.15.
 */
public class TrackedEntityInstancesWrapper {

    /**
     * returns an Object[2] with a list of TrackedEntityInstances in [0], and TrackedEntityAttributeValues
     * in [1]
     * @param body
     * @return
     */
    public static Object[] parseTrackedEntityInstances(byte[] body) throws IOException {

            JsonNode node = Dhis2.getInstance().getObjectMapper().
                    readTree(body);
            JsonNode headersNode = node.get("headers");
            TypeReference<List<Header>> typeRef =
                    new TypeReference<List<Header>>(){};
            List<Header> headers = Dhis2.getInstance().getObjectMapper().
                    readValue( headersNode.traverse(), typeRef);

            JsonNode rowsNode = node.get("rows");

            TypeReference<List<List<String>>> typeRefRows = new TypeReference<List<List<String>>>() {
            };

            List<List<String>> rows = Dhis2.getInstance().getObjectMapper().readValue(rowsNode.traverse(), typeRefRows);
            List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();
            List<TrackedEntityAttributeValue> trackedEntityAttributeValues = new ArrayList<>();

            for(List<String> row: rows) {
                /*from 0-4 is hardcoded: instance, created, lastupdated, ou, te (trackedEntity),
                    * everything >4 is attribute*/
                TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
                trackedEntityInstance.trackedEntityInstance = row.get(0);
                trackedEntityInstance.created = row.get(1);
                trackedEntityInstance.lastUpdated = row.get(2);
                trackedEntityInstance.orgUnit = row.get(3);
                trackedEntityInstance.trackedEntity = row.get(4);
                trackedEntityInstances.add(trackedEntityInstance);
                if(row.size() <= 4) continue;
                for(int i = 5; i<row.size(); i++) {
                    TrackedEntityAttributeValue value = new TrackedEntityAttributeValue();
                    value.trackedEntityAttributeId = headers.get(i).column;
                    value.trackedEntityInstanceId = trackedEntityInstance.trackedEntityInstance;
                    value.value = row.get(i);
                    trackedEntityAttributeValues.add(value);
                 }
            }
            return new Object[] {trackedEntityInstances, trackedEntityAttributeValues};
    }

}
