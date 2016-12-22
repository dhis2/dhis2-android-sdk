package org.hisp.dhis.android.core.data.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.annotation.Annotation;

import retrofit2.Converter;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FilterConverterUnitTests {
    private Converter<Filter, String> fieldsConverter;

    @Before
    public void setUp() {
        fieldsConverter = new FilterConverter();
    }

    @Test
    public void converterFactory_shouldReturnConverterOnSpecificAnnotation() {
        Converter.Factory converterFactory = FilterConverterFactory.create();

        Converter<?, String> converter = converterFactory
                .stringConverter(null, new Annotation[]{new Fields() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Fields.class;
                    }
                }}, null);

        assertThat(converter).isInstanceOf(FilterConverter.class);
    }

    @Test
    public void converter_shouldRespectFields() throws IOException {
        String queryStringOne = fieldsConverter.convert(
                Filter.builder().fields(Field.create("")).build());
        String queryStringTwo = fieldsConverter.convert(
                Filter.builder().fields(Field.create("*")).build());
        String queryStringThree = fieldsConverter.convert(
                Filter.builder().fields(
                        Field.create("name"),
                        Field.create("displayName"),
                        Field.create("created"),
                        Field.create("lastUpdated")
                ).build());

        assertThat(queryStringOne).isEqualTo("");
        assertThat(queryStringTwo).isEqualTo("*");
        assertThat(queryStringThree).isEqualTo("name,displayName,created,lastUpdated");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void converter_shouldRespectNestedFields() throws IOException {
        Field id = Field.create("id");
        Field displayName = Field.create("displayName");
        NestedField programs = NestedField.create("programs");
        NestedField programsWithChildren = programs.with(id, displayName);

        String queryStringOne = fieldsConverter.convert(
                Filter.builder().fields(
                        id, displayName, programs
                ).build());
        String queryStringTwo = fieldsConverter.convert(
                Filter.builder().fields(
                        id, displayName, programsWithChildren
                ).build());

        assertThat(queryStringOne).isEqualTo("id,displayName,programs");
        assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]");
    }
}
