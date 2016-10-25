package org.hisp.dhis.client.sdk.ui.adapters.expandable;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.ReportEntity;

public class ReportEntityChildViewHolder<C> extends ChildViewHolder<C> {

    final View errorView;

    final View refreshButton;

    final TextView label;

    final TextView date;

    public ReportEntityChildViewHolder(View itemView) {
        super(itemView);

        errorView = itemView.findViewById(R.id.error_text);
        refreshButton = itemView
                .findViewById(R.id.refresh_button);
        label = (TextView) itemView
                .findViewById(R.id.event_name);
        date = (TextView) itemView.findViewById(R.id.date_text);
    }

    public void bind(ReportEntity reportEntity) {
        label.setText(reportEntity.getId());
        //date.setText();
    }
}
