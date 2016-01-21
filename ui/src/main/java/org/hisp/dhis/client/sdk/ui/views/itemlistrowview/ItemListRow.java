package org.hisp.dhis.client.sdk.ui.views.itemlistrowview;

import android.support.v4.util.Pair;
import android.view.View;

import java.util.List;

public class ItemListRow {
    private List<Pair<String, Integer>> valuesPosition;
    private Object object;
    private String status;
    private View.OnClickListener onRowClickListener;
    private View.OnClickListener onStatusClickListener;
    private View.OnLongClickListener onLongClickListener;

    public ItemListRow(Object object, List<Pair<String,Integer>> valuesPosition, String status) {
        this.object = object;
        this.valuesPosition = valuesPosition;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public List<Pair<String, Integer>> getValuesPosition() {
        return valuesPosition;
    }

    public void setValuesPosition(List<Pair<String, Integer>> valuesPosition) {
        this.valuesPosition = valuesPosition;
    }

    public View.OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public View.OnClickListener getOnRowClickListener() {
        return onRowClickListener;
    }

    public void setOnRowClickListener(View.OnClickListener onRowClickListener) {
        this.onRowClickListener = onRowClickListener;
    }

    public View.OnClickListener getOnStatusClickListener() {
        return onStatusClickListener;
    }

    public void setOnStatusClickListener(View.OnClickListener onStatusClickListener) {
        this.onStatusClickListener = onStatusClickListener;
    }

    @Override
    public String toString() {
        return "";
    }
}
