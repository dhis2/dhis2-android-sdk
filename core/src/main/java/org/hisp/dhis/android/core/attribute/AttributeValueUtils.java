package org.hisp.dhis.android.core.attribute;

import java.util.ArrayList;
import java.util.List;

public class AttributeValueUtils {
    public static List<Attribute> extractAttributes(List<AttributeValue> attributeValues) {
        List<Attribute> attributes = new ArrayList<>();

        for (AttributeValue attValue : attributeValues) {
            attributes.add(attValue.attribute());
        }

        return attributes;
    }

    public static String extractValue(List<AttributeValue> attributeValues, String attributeUId) {
        String value = "";

        for (AttributeValue attValue : attributeValues) {
            if (attValue.attribute().uid().equals(attributeUId)) {
                value = attValue.value();
                break;
            }
        }

        return value;
    }
}
