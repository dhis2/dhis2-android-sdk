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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.events.OnRowClick;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.events.OnTrackerItemClick;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * Created by erling on 5/11/15.
 */
public class TrackedEntityInstanceItemRow implements EventRow, Comparator<TrackedEntityInstanceItemRow>, View.OnCreateContextMenuListener {
    protected TrackedEntityInstance mTrackedEntityInstance;
    protected List<String> columns;
    private OnRowClick.ITEM_STATUS mStatus;

    private Drawable mOfflineDrawable;
    private Drawable mErrorDrawable;
    private Drawable mSentDrawable;

    private String mSent;
    private String mError;
    private String mOffline;

    public TrackedEntityInstanceItemRow(Context context) {
        isNull(context, "Context must not be null");

        mOfflineDrawable = context.getResources().getDrawable(R.drawable.ic_legacy_offline);
        mErrorDrawable = context.getResources().getDrawable(R.drawable.ic_event_error);
        mSentDrawable = context.getResources().getDrawable(R.drawable.ic_from_server);

        mSent = context.getResources().getString(R.string.event_sent);
        mError = context.getResources().getString(R.string.event_error);
        mOffline = context.getResources().getString(R.string.event_offline);
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup container) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.listview_event_item, container, false);

            holder = new ViewHolder(
                    (LinearLayout)view.findViewById(R.id.dynamic_column_container),
                    (ImageView) view.findViewById(R.id.status_image_view),
                    (TextView) view.findViewById(R.id.status_text_view),
                    (LinearLayout) view.findViewById(R.id.status_container)
            );


        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        view.setTag(holder);

        OnTrackedEntityInstanceInternalClickListener onTrackedEntityInstanceInternalClickListener =new OnTrackedEntityInstanceInternalClickListener();

        onTrackedEntityInstanceInternalClickListener.setTrackedEntityInstance(mTrackedEntityInstance);
        onTrackedEntityInstanceInternalClickListener.setStatus(mStatus);

        view.setOnClickListener(onTrackedEntityInstanceInternalClickListener);
        view.setOnLongClickListener(onTrackedEntityInstanceInternalClickListener);
        holder.statusContainer.setOnClickListener(onTrackedEntityInstanceInternalClickListener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                (1.0f-0.25f)/columns.size()
        );
        holder.container.removeAllViews();
        for(String column: columns){
            LinearLayout columnView = (LinearLayout)inflater.inflate(R.layout.event_item_column, holder.container , false);
            TextView textView = (TextView) columnView.findViewById(R.id.column_name);
            textView.setText(column);
            holder.container.addView(columnView, params);
            View spaceView = inflater.inflate(R.layout.space_event_column, holder.container , false);
            holder.container.addView(spaceView);
        }
        if (mStatus != null) {
            switch (mStatus) {
                case OFFLINE: {
                    holder.statusImageView.setImageDrawable(mOfflineDrawable);
                    holder.statusTextView.setText(mOffline);
                    break;
                }
                case ERROR: {
                    holder.statusImageView.setImageDrawable(mErrorDrawable);
                    holder.statusTextView.setText(mError);
                    break;
                }
                case SENT: {
                    holder.statusImageView.setImageDrawable(mSentDrawable);
                    holder.statusTextView.setText(mSent);
                    break;
                }
            }
        }

        return view;
    }

    @Override
    public int getViewType() {
        return EventRowType.EVENT_ITEM_ROW.ordinal();
    }

    @Override
    public long getId() {
        if (mTrackedEntityInstance != null) {
            return mTrackedEntityInstance.getLocalId();
        } else {
            return 0;
        }
    }

    public TrackedEntityInstance getTrackedEntityInstance() {
        return mTrackedEntityInstance;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        mTrackedEntityInstance = trackedEntityInstance;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public void addColumn(String column) {
        if(columns == null){
            columns = new ArrayList<>();
        }
        columns.add(column);
    }

    public void setStatus(OnRowClick.ITEM_STATUS status) {
        mStatus = status;
    }

    public OnRowClick.ITEM_STATUS getStatus() {
        return mStatus;
    }

    @Override
    public int compare(TrackedEntityInstanceItemRow lhs, TrackedEntityInstanceItemRow rhs) {
        return 0;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("hello","oncreatecontext");
    }



    private static class ViewHolder {
        public final LinearLayout container;
        public final ImageView statusImageView;
        public final TextView statusTextView;
        public final LinearLayout statusContainer;

        public ViewHolder(LinearLayout container, ImageView statusImageView, TextView statusTextView,
                LinearLayout
                        statusContainer) {
            this.container = container;
            this.statusImageView = statusImageView;
            this.statusTextView = statusTextView;
            this.statusContainer = statusContainer;
        }
    }

    private static class OnTrackedEntityInstanceInternalClickListener implements View.OnClickListener, View.OnLongClickListener{
        private TrackedEntityInstance trackedEntityInstance;
        private OnRowClick.ITEM_STATUS status;

        public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
            this.trackedEntityInstance = trackedEntityInstance;
        }

        public void setStatus(OnRowClick.ITEM_STATUS status) {
            this.status = status;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.event_container) {
                Dhis2Application.getEventBus()
                        .post(new OnTrackerItemClick(trackedEntityInstance, status, true));
            } else if (view.getId() == R.id.status_container) {
                Dhis2Application.getEventBus()
                        .post(new OnTrackerItemClick(trackedEntityInstance, status, false));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            // empty implementation for making the context menu work

            return false;
        }
    }
}
