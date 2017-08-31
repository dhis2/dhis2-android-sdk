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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.events.OnRowClick;
import org.hisp.dhis.android.sdk.events.OnTrackerItemClick;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Event;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

import java.util.List;

/**
 * Created by araz on 03.04.2015.
 */
public final class EventItemRow implements EventRow {
    private Event mEvent;
    private List<String> columns;
    private OnRowClick.ITEM_STATUS mStatus;

    private Drawable mOfflineDrawable;
    private Drawable mErrorDrawable;
    private Drawable mSentDrawable;

    private String mSent;
    private String mError;
    private String mOffline;

    public EventItemRow(Context context) {
        isNull(context, "Context must not be null");

        mOfflineDrawable = context.getResources().getDrawable(R.drawable.ic_offline);
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
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        OnEventInternalClickListener listener = new OnEventInternalClickListener();
        listener.setEvent(mEvent);
        listener.setStatus(mStatus);
        view.setOnClickListener(listener);
        view.setOnLongClickListener(listener);
        holder.statusContainer.setOnClickListener(listener);

        for(String column: columns){
            View columnView = inflater.inflate(R.layout.item_column, holder.columnContainer , false);
            TextView textView = (TextView) columnView.findViewById(R.id.column_name);
            textView.setText(column);
        }

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

        return view;
    }

    @Override
    public int getViewType() {
        return EventRowType.EVENT_ITEM_ROW.ordinal();
    }

    @Override
    public long getId() {
        if (mEvent != null) {
            return mEvent.getLocalId();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setEvent(Event event) {
        mEvent = event;
    }

    public Event getmEvent() {
        return mEvent;
    }

    public void setStatus(OnRowClick.ITEM_STATUS status) {
        mStatus = status;
    }

    public OnRowClick.ITEM_STATUS getStatus() {
        return mStatus;
    }

    private static class ViewHolder {
        public final LinearLayout columnContainer;
        public final LinearLayout statusContainer;
        public final ImageView statusImageView;
        public final TextView statusTextView;

        private ViewHolder(LinearLayout columnContainer,
                           ImageView statusImageView,
                           TextView statusTextView,
                           LinearLayout statusContainer
                           ) {
            this.columnContainer = columnContainer;
            this.statusImageView = statusImageView;
            this.statusTextView = statusTextView;
            this.statusContainer = statusContainer;
        }
    }

    private static class OnEventInternalClickListener implements View.OnClickListener, View.OnLongClickListener {
        private Event event;
        private OnRowClick.ITEM_STATUS status;

        public void setEvent(Event event) {
            this.event = event;
        }

        public void setStatus(OnRowClick.ITEM_STATUS status) {
            this.status = status;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.event_container) {
                Dhis2Application.getEventBus()
                        .post(new OnTrackerItemClick(event, status, true));
            } else if (view.getId() == R.id.status_container) {
                Dhis2Application.getEventBus()
                        .post(new OnTrackerItemClick(event, status, false));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            //empty implementation for registering context menu
            return false;
        }
    }
}
