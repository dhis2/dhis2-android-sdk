package org.hisp.dhis.android.core.data.api;

import org.hisp.dhis.android.models.common.Field;
import org.hisp.dhis.android.models.common.NestedField;
import org.hisp.dhis.android.models.common.Property;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import retrofit2.Converter;

class FilterConverter implements Converter<Filter, String> {
    FilterConverter() {
        // explicit empty constructor
    }

    @Override
    @SuppressWarnings("unchecked")
    public String convert(Filter filter) throws IOException {
        StringBuilder builder = new StringBuilder();

        // recursive function which processes
        // properties and builds query string
        append(builder, (List<Property>) filter.fields());

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private static void append(StringBuilder builder, List<Property> properties) {
        Iterator<Property> propertyIterator = properties.iterator();

        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.next();

            // we need to append property name first
            builder.append(property.name());

            if (property instanceof Field) {
                if (propertyIterator.hasNext()) {
                    builder.append(",");
                }
            } else if (property instanceof NestedField) {
                List<Property> children = ((NestedField) property).children();

                if (!children.isEmpty()) {
                    // open property array
                    builder.append("[");

                    // recursive call to method
                    append(builder, children);

                    // close property array
                    builder.append("]");
                }
            } else {
                throw new IllegalArgumentException("Unsupported type of Property: " +
                        property.getClass());
            }
        }
    }
}
