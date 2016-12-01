package org.hisp.dhis.client.sdk.ui.utils;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;

public class FormUtils {
    public static String getFormEntityLabel(FormEntity formEntity) {
        if (formEntity.isMandatory()) {
            return String.format("%s (*)", formEntity.getLabel());
        }
        return formEntity.getLabel();
    }
}
