package org.hisp.dhis.client.sdk.ui.models;


import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

public class ExpansionPanel implements Parent<ReportEntity> {

    private final String id;
    private final String label;
    private final Type type;
    private List<ReportEntity> children;

    public ExpansionPanel(String id, String label, Type type) {
        this.id = id;
        this.label = label;
        this.type = type;
    }

    @Override
    public List<ReportEntity> getChildList() {
        return children;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public enum Type {
        ACTION_ADD, ACTION_EDIT, ACTION_NONE
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }

    public void setChildren(List<ReportEntity> children) {
        this.children = children;
    }
}
