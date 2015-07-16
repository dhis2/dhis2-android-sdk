package org.hisp.dhis.android.sdk.events;

import android.util.Log;

/**
 * Created by erling on 6/29/15.
 */
public class OnTrackerItemColumnClick
{
    public static final int FIRST_COLUMN = 1;
    public static final int SECOND_COLUMN = 2;
    public static final int THIRD_COLUMN = 3;
    public static final int STATUS_COLUMN = 4;

    private final int columnClicked;

    public OnTrackerItemColumnClick(int columnClicked)
    {
        this.columnClicked = columnClicked;
    }

    public int getColumnClicked() {
        return columnClicked;
    }
}
