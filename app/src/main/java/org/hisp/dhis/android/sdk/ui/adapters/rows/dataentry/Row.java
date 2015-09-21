package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

/**
 * Created by erling on 9/9/15.
 */
public abstract class Row implements DataEntryRow
{

    protected String mLabel;
    protected BaseValue mValue;
    protected String mDescription;
    protected DataEntryRowTypes mRowType;
    protected View detailedInfoButton;
    private boolean hideDetailedInfoButton;
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

    public String getItemId()
    {
        if(mValue instanceof DataValue)
            return ((DataValue) mValue).getDataElement();
        else if(mValue instanceof TrackedEntityAttributeValue)
            return ((TrackedEntityAttributeValue) mValue).getTrackedEntityAttributeId();
        else
            return "";
    }

    public String getDescription() {
        if(this instanceof CoordinatesRow)
            mDescription =  "";
        else if (this instanceof StatusRow)
            mDescription = "";
        else if(this instanceof IndicatorRow)
            mDescription = "";

        String itemId = getItemId();
        DataElement dataElement = MetaDataController.getDataElement(itemId);
        if(dataElement != null)
            mDescription = dataElement.getDescription();
        else
        {
            TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute(itemId);
            if(attribute != null)
                mDescription = attribute.getDescription();
        }

        return mDescription;
    }
    public void checkNeedsForDescriptionButton()
    {
        mDescription = getDescription();
        if(mDescription == null || mDescription.equals(""))
            setHideDetailedInfoButton(true);
    }
    public boolean isDetailedInfoButtonHidden() {
        return hideDetailedInfoButton;
    }

    public void setHideDetailedInfoButton(boolean hideDetailedInfoButton) {
        this.hideDetailedInfoButton = hideDetailedInfoButton;
    }
}
