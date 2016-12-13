package org.hisp.dhis.android.core.data.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class FieldsConverter extends Converter.Factory {
    @Override
    public Converter<?, String> stringConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        return null;
    }
}
