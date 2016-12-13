package org.hisp.dhis.android.core.data.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class FieldsConverterFactory extends Converter.Factory {
    public static FieldsConverterFactory create() {
        return new FieldsConverterFactory();
    }

    private FieldsConverterFactory() {
        // private constructor
    }

    @Override
    public Converter<?, String> stringConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Fields) {
                return new FieldsConverter();
            }
        }

        return null;
    }
}
