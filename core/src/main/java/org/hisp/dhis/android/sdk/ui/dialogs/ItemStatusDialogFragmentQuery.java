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

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.events.OnRowClick;
import org.hisp.dhis.android.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;

/**
 * Created by Simen S. Russnes on 7/9/15.
 */
public class ItemStatusDialogFragmentQuery implements Query<ItemStatusDialogFragmentForm>
{
    public static final String TAG = ItemStatusDialogFragmentQuery.class.getSimpleName();
    private long id;
    private String type;


    public ItemStatusDialogFragmentQuery(long id, String type)
    {
        this.id = id;
        this.type = type;
    }

    @Override
    public ItemStatusDialogFragmentForm query(Context context)
    {
        BaseSerializableModel item = null;
        switch (type) {
            case FailedItem.TRACKEDENTITYINSTANCE: {
                item = TrackerController.getTrackedEntityInstance(id);
                break;
            }
            case FailedItem.ENROLLMENT: {
                item = TrackerController.getEnrollment(id);
                break;
            }
            case FailedItem.EVENT: {
                item = TrackerController.getEvent(id);
                break;
            }
        }
        ItemStatusDialogFragmentForm form = new ItemStatusDialogFragmentForm();
        form.setItem(item);
        form.setType(type);

        if(item == null) {
            return form;
        }

        boolean failed = false;
        if(TrackerController.getFailedItem(type, id) != null) {
            failed = true;
        }

        if (failed) {
            form.setStatus(OnRowClick.ITEM_STATUS.ERROR);
        } else if (item.isFromServer()) {
            form.setStatus(OnRowClick.ITEM_STATUS.SENT);
        } else {
            form.setStatus(OnRowClick.ITEM_STATUS.OFFLINE);
        }
        return form;
    }
}
