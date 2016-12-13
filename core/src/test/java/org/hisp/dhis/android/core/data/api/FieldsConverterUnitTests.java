package org.hisp.dhis.android.core.data.api;

import org.hisp.dhis.android.models.common.Field;
import org.hisp.dhis.android.models.common.NestedField;
import org.hisp.dhis.android.models.common.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import retrofit2.Converter;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FieldsConverterUnitTests {
    private Converter<List<Property>, String> fieldsConverter;

    @Before
    public void setUp() {
        fieldsConverter = new FieldsConverter();
    }

    @Test
    public void converterFactory_shouldReturnConverterOnSpecificAnnotation() {
        Converter.Factory converterFactory = FieldsConverterFactory.create();

        Converter<?, String> converter = converterFactory
                .stringConverter(null, new Annotation[]{new Fields() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Fields.class;
                    }
                }}, null);

        assertThat(converter).isInstanceOf(FieldsConverter.class);
    }

    @Test
    public void converter_shouldRespectFields() throws IOException {
        String queryStringOne = fieldsConverter.convert(
                Arrays.asList((Property) Field.create("")));
        String queryStringTwo = fieldsConverter.convert(
                Arrays.asList((Property) Field.create("*")));
        String queryStringThree = fieldsConverter.convert(
                Arrays.asList(
                        (Property) Field.create("name"),
                        (Property) Field.create("displayName"),
                        (Property) Field.create("created"),
                        (Property) Field.create("lastUpdated")));

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
                Arrays.asList(id, displayName, programs));
        String queryStringTwo = fieldsConverter.convert(
                Arrays.asList(id, displayName, programsWithChildren));

        assertThat(queryStringOne).isEqualTo("id,displayName,programs");
        assertThat(queryStringTwo).isEqualTo("id,displayName,programs[id,displayName]");
    }
}
