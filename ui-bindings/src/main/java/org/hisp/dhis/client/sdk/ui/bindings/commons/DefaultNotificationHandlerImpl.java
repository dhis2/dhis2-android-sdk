/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.hisp.dhis.client.sdk.ui.bindings.R;

public class DefaultNotificationHandlerImpl implements DefaultNotificationHandler {

    private final Context context;

    public DefaultNotificationHandlerImpl(Context context) {
        this.context = context;
    }

    public void showIsSyncingNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.sync_in_progress_notification_title))
                .setContentText(context.getString(R.string.sync_in_progress_notification_content))
                .setProgress(0, 0, true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS);

        showSyncNotification(builder);
    }

    public void showSyncCompletedNotification(boolean completedSuccessfully) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (completedSuccessfully) {
            builder.setContentTitle(context.getString(R.string.sync_complete_notification_title))
                    .setContentText(context.getString(R.string.sync_complete_notification_content))
                    .setCategory(NotificationCompat.CATEGORY_STATUS);
        } else {
            builder.setContentTitle(context.getString(R.string.sync_failed_notification_title))
                    .setContentText(context.getString(R.string.sync_failed_notification_content))
                    .setCategory(NotificationCompat.CATEGORY_ERROR);
        }

        // remove progressbar
        builder.setProgress(0, 0, false);

        showSyncNotification(builder);
    }

    @Override
    public void removeAllNotifications() {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void showSyncNotification(NotificationCompat.Builder builder) {

        if (NavigationHandler.homeActivity() != null) {

            Class homeActivity = NavigationHandler.homeActivity();
            Intent resultIntent = new Intent(context, homeActivity);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(homeActivity);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
        }

        builder.setSmallIcon(R.drawable.ic_notification);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // using R.string int because it is unique
        notificationManager.notify(R.string.account_type, builder.build());
    }
}
