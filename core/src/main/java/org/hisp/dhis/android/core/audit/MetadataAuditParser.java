package org.hisp.dhis.android.core.audit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.io.IOException;

public class MetadataAuditParser {
    public MetadataAudit parse(String json, Class<?> genericParameterClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JavaType type = getType(genericParameterClass, objectMapper);

        return objectMapper.readValue(json, type);
    }

    private JavaType getType(Class<?> klass, ObjectMapper objectMapper) {

        return objectMapper.getTypeFactory()
                .constructParametricType(MetadataAudit.class, klass);
    }
}
