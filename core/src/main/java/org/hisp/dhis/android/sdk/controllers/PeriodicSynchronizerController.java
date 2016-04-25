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

package org.hisp.dhis.android.sdk.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.hisp.dhis.android.sdk.services.PeriodicSynchronizer;

/**
 * @author Simen Skogly Russnes on 25.08.15.
 */
public final class PeriodicSynchronizerController {

    public final static String UPDATE_FREQUENCY = "update_frequency";
    public static final String CLASS_TAG = PeriodicSynchronizerController.class.getSimpleName();

    private PeriodicSynchronizerController() {}

    /**
     * Returns the currently set Update Frequency
     *
     * @param context
     * @return
     */
    public static int getUpdateFrequency(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DhisController.PREFS_NAME, Context.MODE_PRIVATE);
        int updateFrequency = sharedPreferences.getInt(UPDATE_FREQUENCY, PeriodicSynchronizer.DEFAULT_UPDATE_FREQUENCY);
        Log.e(CLASS_TAG, "updateFrequency: " + updateFrequency);
        return updateFrequency;
    }

    /**
     * Sets the update frequency by an integer referencing the indexes in the update_frequencies string-array
     *
     * @param context
     * @param frequency index of update frequencies. See update_frequencies string-array
     */
    public static void setUpdateFrequency(Context context, int frequency) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DhisController.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UPDATE_FREQUENCY, frequency);
        editor.commit();
        Log.e(CLASS_TAG, "updateFrequency: " + frequency);
        PeriodicSynchronizer.reActivate(context);
    }

    public static void cancelPeriodicSynchronizer(Context context) {
        PeriodicSynchronizer.cancelPeriodicSynchronizer(context);
    }

    public static void activatePeriodicSynchronizer(Context context) {
        PeriodicSynchronizer.activatePeriodicSynchronizer(context, PeriodicSynchronizer.getInterval(context));
    }
}
