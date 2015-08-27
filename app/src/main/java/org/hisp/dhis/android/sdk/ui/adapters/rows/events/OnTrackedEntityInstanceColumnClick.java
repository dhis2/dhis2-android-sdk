package org.hisp.dhis.android.sdk.ui.adapters.rows.events;

/**
 * Created by erling on 8/21/15.
 */
public class OnTrackedEntityInstanceColumnClick
{

    public static final int FIRST_COLUMN = 1;
    public static final int SECOND_COLUMN = 2;
    public static final int THIRD_COLUMN = 3;
    public static final int STATUS_COLUMN = 4;

    private final int columnClicked;

    public OnTrackedEntityInstanceColumnClick(int columnClicked)
    {
        this.columnClicked = columnClicked;
    }

    public int getColumnClicked() {
        return columnClicked;
    }


}
