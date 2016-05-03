package org.hisp.dhis.client.sdk.ui.models;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FormSection {
    private final String id;
    private final String label;

    public FormSection(String id, String label) {
        this.id = isNull(id, "id must not be null");
        this.label = isNull(label, "label must not be null");
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "FormSection{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FormSection that = (FormSection) other;
        if (!id.equals(that.id)) {
            return false;
        }

        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }
}
