package org.hisp.dhis.client.sdk.ui.utils;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;

import java.util.ArrayList;
import java.util.List;

public class FormUtils {
    private static final CharSequence EMPTY_STRING = "";

    public static String getFormEntityLabel(FormEntity formEntity) {
        if (formEntity.isMandatory()) {
            return String.format("%s (*)", formEntity.getLabel());
        }
        return formEntity.getLabel();
    }

    public static List<FormEntity> getInvalidFormEntities(List<FormEntity> allFormEntities) {
        List<FormEntity> invalidFormEntities = new ArrayList<>();

        for (FormEntity formEntity : allFormEntities) {
            if (formEntity.isMandatory() && formEntity.getValue().equals(EMPTY_STRING)) {
                invalidFormEntities.add(formEntity);
            }
        }
        return invalidFormEntities;
    }
}
