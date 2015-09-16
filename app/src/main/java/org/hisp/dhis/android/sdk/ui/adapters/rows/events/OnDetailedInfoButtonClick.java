package org.hisp.dhis.android.sdk.ui.adapters.rows.events;

import android.util.Log;
import android.view.View;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;

/**
 * Created by erling on 9/8/15.
 */
public class OnDetailedInfoButtonClick implements View.OnClickListener
{
    final Row row;

    public OnDetailedInfoButtonClick(Row row)
    {
        this.row = row;
    }

    public Row getRow() {
        return row;
    }

    @Override
    public void onClick(View view)
    {
        Dhis2Application.getEventBus().post(new OnDetailedInfoButtonClick(row)); // DataEntryFragment is sniffing this event
    }
}
