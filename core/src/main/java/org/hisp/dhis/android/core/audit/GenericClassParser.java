package org.hisp.dhis.android.core.audit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.io.IOException;

public class GenericClassParser {

    public <T> T parse(String json, Class<?> type, Class<?> parameterType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JavaType javaType = getType(objectMapper, type, parameterType);

        return objectMapper.readValue(json, javaType);
    }

    private JavaType getType(ObjectMapper objectMapper, Class<?> type, Class<?> parameterType) {

        return objectMapper.getTypeFactory()
                .constructParametricType(type, parameterType);
    }
}
