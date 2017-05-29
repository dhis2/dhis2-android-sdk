package org.hisp.dhis.client.sdk.ui.models;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FormEntityAction {
    private final String id;
    private final String value;
    private final FormEntityActionType actionType;

    public FormEntityAction(String id, String value, FormEntityActionType actionType) {
        this.id = isNull(id, "id must not be null");
        this.actionType = isNull(actionType, "actionType must not be null");
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public FormEntityActionType getActionType() {
        return actionType;
    }

    public enum FormEntityActionType {
        HIDE, ASSIGN
    }

    @Override
    public String toString() {
        return "FormEntityAction{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
