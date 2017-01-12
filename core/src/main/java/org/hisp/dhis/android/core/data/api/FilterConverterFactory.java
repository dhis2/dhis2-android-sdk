package org.hisp.dhis.android.core.data.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

public final class FilterConverterFactory extends Converter.Factory {
    public static FilterConverterFactory create() {
        return new FilterConverterFactory();
    }

    private FilterConverterFactory() {
        // private constructor
    }

    @Override
    public Converter<?, String> stringConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Fields) {
                return new FilterConverter();
            }
        }

        return null;
    }
}
