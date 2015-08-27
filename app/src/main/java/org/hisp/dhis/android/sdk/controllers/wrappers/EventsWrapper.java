package org.hisp.dhis.android.sdk.controllers.wrappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.persistence.models.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 26.08.15.
 */
public class EventsWrapper {

    public static List<Event> getEvents(JsonNode jsonNode) {
        TypeReference<List<Event>> typeRef =
                new TypeReference<List<Event>>() {};
        List<Event> events;
        try {
            if(jsonNode.has("events")) {
                events = DhisController.getInstance().getObjectMapper().
                        readValue(jsonNode.get("events").traverse(), typeRef);
            } else {
                events = new ArrayList<>();
            }
        } catch (IOException e) {
            events = new ArrayList<>();
            e.printStackTrace();
        }
        return events;
    }

}
