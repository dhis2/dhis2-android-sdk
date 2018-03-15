package org.hisp.dhis.android.sdk.utils.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;

import java.io.IOException;

public class TrackedEntityInstanceTrackedEntitySerializer extends JsonSerializer<TrackedEntityInstance> {

    @Override
    public void serialize(TrackedEntityInstance value, JsonGenerator jgen,
            SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.setCurrentValue(value);
        if(!DhisController.getInstance().isLoggedInServerWithLatestApiVersion()) {
            jgen.writeStringField("trackedEntity", value.getTrackedEntity());
        }else {
            jgen.writeOmittedField("trackedEntity");
        }
        if(DhisController.getInstance().isLoggedInServerWithLatestApiVersion()) {
            jgen.writeStringField("trackedEntityType", value.getTrackedEntity());
        } else {
            jgen.writeOmittedField("trackedEntityType");
        }
        jgen.writeObjectField("attributes", value.getAttributes());
        jgen.writeStringField("orgUnit", value.getOrgUnit());
        jgen.writeObjectField("relationships", value.getRelationships());
        jgen.writeStringField("trackedEntityInstance", value.getTrackedEntityInstance());
        if(value.getCreated()!=null) {
            jgen.writeStringField("created", value.getCreated());
            jgen.writeStringField("lastUpdated", value.getLastUpdated());
        }
        jgen.writeEndObject();
    }

    @Override
    public Class<TrackedEntityInstance> handledType() {
        return TrackedEntityInstance.class;
    }
}