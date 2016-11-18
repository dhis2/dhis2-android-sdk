package org.hisp.dhis.client.sdk.ui.adapters.expandable;

/**
 * An interface meant to be used to communicate from the ViewHolder/View of Recycler view,
 * to an object that cares about the selected id. Usually Fragment that contains the RecyclerView.
 */
public interface RecyclerViewSelection {

    void setSelectedUid(String id);

    String getSelectedUid();
}
