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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

/**
 * Created by erling on 5/8/15.
 */
public class TrackedEntityInstanceColumnNamesRow implements EventRow
{
    private String mFirstItem;
    private String mSecondItem;
    private String mThirdItem;
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
                    (TextView) view.findViewById(R.id.first_column_name),
                    (TextView) view.findViewById(R.id.second_column_name),
                    (TextView) view.findViewById(R.id.third_column_name),
                    (TextView) view.findViewById(R.id.status_column),
                    new ViewHolder.OnInternalColumnRowClickListener()
            );
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.trackedEntityTitle.setText(mTitle);
        if (mFirstItem == null) {
            holder.firstItem.setVisibility(View.GONE);
        } else {
            holder.firstItem.setVisibility(View.VISIBLE);
            holder.firstItem.setText(mFirstItem);
            holder.firstItem.setOnClickListener(holder.listener);
        }

        if (mSecondItem == null) {
            holder.secondItem.setVisibility(View.GONE);
        } else {
            holder.secondItem.setVisibility(View.VISIBLE);
            holder.secondItem.setText(mSecondItem);
            holder.secondItem.setOnClickListener(holder.listener);
        }

        if (mThirdItem == null) {
            holder.thirdItem.setVisibility(View.GONE);
        } else {
            holder.thirdItem.setVisibility(View.VISIBLE);
            holder.thirdItem.setText(mThirdItem);
            holder.thirdItem.setOnClickListener(holder.listener);
        }
        holder.statusItem.setOnClickListener(holder.listener);


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

    public void setSecondItem(String secondItem) {
        this.mSecondItem = secondItem;
    }

    public void setFirstItem(String firstItem) {
        this.mFirstItem = firstItem;
    }

    public void setThirdItem(String mThirdItem) {
        this.mThirdItem = mThirdItem;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmFirstItem() {
        return mFirstItem;
    }

    public String getmSecondItem() {
        return mSecondItem;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmThirdItem() {
        return mThirdItem;
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
        public final TextView firstItem;
        public final TextView secondItem;
        public final TextView thirdItem;
        public final TextView statusItem;
        public final OnInternalColumnRowClickListener listener;


        private ViewHolder(TextView trackedEntityTitle,
                           TextView firstItem,
                           TextView secondItem,
                           TextView thirdItem,
                           TextView statusItem,
                           OnInternalColumnRowClickListener listener) {
            this.trackedEntityTitle = trackedEntityTitle;
            this.firstItem = firstItem;
            this.secondItem = secondItem;
            this.thirdItem = thirdItem;
            this.statusItem = statusItem;
            this.listener = listener;
        }

        private static class OnInternalColumnRowClickListener implements View.OnClickListener
        {
            @Override
            public void onClick(View view)
            {
                if(view.getId() == R.id.first_column_name)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.FIRST_COLUMN));

                else if(view.getId() == R.id.second_column_name)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.SECOND_COLUMN));

                else if (view.getId() == R.id.third_column_name)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.THIRD_COLUMN));

                else if(view.getId() == R.id.status_column)
                    Dhis2Application.getEventBus().post(
                            new OnTrackedEntityInstanceColumnClick(OnTrackedEntityInstanceColumnClick.STATUS_COLUMN));
            }

        }
    }
}
