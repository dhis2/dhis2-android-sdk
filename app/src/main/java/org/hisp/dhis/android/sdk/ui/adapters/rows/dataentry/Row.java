package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;

import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;

/**
 * Created by erling on 9/9/15.
 */
public abstract class Row implements DataEntryRow
{

    protected String mLabel;
    protected BaseValue mValue;
    protected DataEntryRowTypes mRowType;
    protected View detailedInfoButton;
    private boolean hidden = false;
    private boolean editable = true;

    public void setIsHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isHidden(){
        return hidden;
    }

    public View getDetailedInfoButton(){
        return detailedInfoButton;
    }

    public BaseValue getValue(){
        return mValue;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public abstract View getView(FragmentManager fragmentManager, LayoutInflater inflater, View convertView, ViewGroup container);


    @Override
    public abstract int getViewType();

    public String getDataElementId()
    {
        if(mValue instanceof DataValue)
            return ((DataValue) mValue).getDataElement();
        else if(mValue instanceof TrackedEntityAttributeValue)
            return ((TrackedEntityAttributeValue) mValue).getTrackedEntityAttributeId();
        else
            return "";
    }
}
