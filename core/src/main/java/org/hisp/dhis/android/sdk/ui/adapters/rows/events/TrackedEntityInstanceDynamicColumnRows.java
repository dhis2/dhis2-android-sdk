/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.adapters.rows.events;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erling on 5/8/15.
 */
public class TrackedEntityInstanceDynamicColumnRows implements EventRow
{
    private List<String> columns;
    private String mTitle;
    private String mTrackedEntity;
    private View view;

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {

        ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.listview_column_names_item, container, false);

            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.tracked_entity_title),
                    (TextView) view.findViewById(R.id.column_name),
                    (TextView) view.findViewById(R.id.status_column),
                    (LinearLayout) view.findViewById(R.id.dynamic_column_container));
            for(String column: columns){
                View columnView = inflater.inflate(R.layout.item_column, (LinearLayout) view.findViewById(R.id.dynamic_column_container), false);
                TextView textView = (TextView) columnView.findViewById(R.id.column_name);
                textView.setText(column);
            }

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        ViewHolder.OnInternalColumnRowClickListener onInternalColumnRowClickListener = new ViewHolder.OnInternalColumnRowClickListener();


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                (1.0f-0.25f)/columns.size()
        );

        params.gravity = Gravity.CENTER_VERTICAL;
        holder.columnContainer.removeAllViews();
        for(String column: columns){
            View columnView = inflater.inflate(R.layout.item_column, holder.columnContainer, false);
            TextView textView = (TextView) columnView.findViewById(R.id.column_name);
            textView.setText(column);
            holder.columnContainer.addView(columnView, params);
            View spaceView = inflater.inflate(R.layout.space_column, holder.columnContainer, false);
            holder.columnContainer.addView(spaceView);
        }

        holder.trackedEntityTitle.setText(mTitle);

        holder.statusItem.setOnClickListener(onInternalColumnRowClickListener);

        return view;
    }

    @Override
    public int getViewType() {
        return EventRowType.COLUMN_NAMES_ROW.ordinal();
    }

    @Override
    public long getId() {
        return -1;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public void addColumn(String column) {
        if(columns == null ){
            columns = new ArrayList<>();
        }
        columns.add(column);
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTrackedEntity() {
        return mTrackedEntity;
    }

    public void setTrackedEntity(String mTrackedEntity) {
        this.mTrackedEntity = mTrackedEntity;
    }

    public View getView() {
        return view;
    }

    private static class ViewHolder {
        public final TextView trackedEntityTitle;
        public final TextView contentItem;
        public final TextView statusItem;
        public final LinearLayout columnContainer;


        private ViewHolder(TextView trackedEntityTitle,
                           TextView contentItem,
                           TextView statusItem,
                           LinearLayout columnContainer) {
            this.trackedEntityTitle = trackedEntityTitle;
            this.contentItem = contentItem;
            this.statusItem = statusItem;
            this.columnContainer = columnContainer;
        }

        private static class OnInternalColumnRowClickListener implements View.OnClickListener
        {
            @Override
            public void onClick(View view)
            {
                if(view.getId() == R.id.column_name)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.FIRST_COLUMN));
                else if(view.getId() == R.id.status_column)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.STATUS_COLUMN));
            }

        }
    }
}
