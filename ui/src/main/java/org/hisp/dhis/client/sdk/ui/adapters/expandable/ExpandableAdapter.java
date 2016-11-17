package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ExpansionPanel;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;

import java.util.List;

public class ExpandableAdapter extends ExpandableRecyclerAdapter<ExpansionPanel, ReportEntity, ExpansionPanelViewHolder, ReportEntityChildViewHolder> {

    private RecyclerViewSelection recyclerViewSelection;

    public void setRecyclerViewSelectionCallback(RecyclerViewSelection recyclerViewSelection) {
        this.recyclerViewSelection = recyclerViewSelection;
    }

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */
    public ExpandableAdapter(@NonNull List<ExpansionPanel> parentList) {
        super(parentList);
    }

    /**
     * Set a new list of parents and notify any registered observers that the data set has changed.
     * <p>
     * This setter does not specify what about the data set has changed, forcing
     * any observers to assume that all existing items and structure may no longer be valid.
     * LayoutManagers will be forced to fully rebind and relayout all visible views.</p>
     * <p>
     * It will always be more efficient to use the more specific change events if you can.
     * Rely on {@code #setParentList(List, boolean)} as a last resort. There will be no animation
     * of changes, unlike the more specific change events listed below.
     *
     * @see #notifyParentInserted(int)
     * @see #notifyParentRemoved(int)
     * @see #notifyParentChanged(int)
     * @see #notifyParentRangeInserted(int, int)
     * @see #notifyChildInserted(int, int)
     * @see #notifyChildRemoved(int, int)
     * @see #notifyChildChanged(int, int)
     */
    public void swap(List<ExpansionPanel> newItems) {
        // todo: use diffutil?
        setParentList(newItems, true);
    }

    @NonNull
    @Override
    public ExpansionPanelViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parentViewGroup.getContext());
        return new ExpansionPanelViewHolder(
                inflater.inflate(R.layout.recyclerview_row_expansion_panel, parentViewGroup, false)
        );
    }

    @NonNull
    @Override
    public ReportEntityChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(childViewGroup.getContext());
        return new ReportEntityChildViewHolder(
                inflater.inflate(R.layout.dashboard_event, childViewGroup, false)
        );
    }

    @Override
    public void onBindParentViewHolder(@NonNull ExpansionPanelViewHolder parentViewHolder, int parentPosition, @NonNull ExpansionPanel parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ReportEntityChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull ReportEntity child) {
        childViewHolder.bind(child, recyclerViewSelection);
    }
}
