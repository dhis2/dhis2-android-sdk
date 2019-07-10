/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.d2manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

class D2ConfigurationValidator {
    static D2Configuration validateAndSetDefaultValues(D2Configuration input) throws D2Error {
        return input.toBuilder()
                .appName(mergeAppName(input.appName(), input.context()))
                .appVersion(mergeAppVersion(input.appVersion(), input.context()))
                .build();
    }

    private static String mergeAppName(String inputAppName, Context context) throws D2Error {
        if (inputAppName == null) {
            String androidAppName = getAndroidAppName(context);
            if (androidAppName == null) {
                throw D2Error.builder()
                        .errorComponent(D2ErrorComponent.SDK)
                        .errorCode(D2ErrorCode.APP_NAME_NOT_SET)
                        .errorDescription("The app name was not passed and the SDK was not able to get it from the " +
                                "Android system")
                        .build();
            } else {
                return androidAppName;
            }
        } else {
            return inputAppName;
        }
    }

    private static String mergeAppVersion(String inputVersionNumber, Context context) throws D2Error {
        if (inputVersionNumber == null) {
            String androidVersionNumber = getAndroidVersionNumber(context);
            if (androidVersionNumber == null) {
                throw D2Error.builder()
                        .errorComponent(D2ErrorComponent.SDK)
                        .errorCode(D2ErrorCode.APP_VERSION_NOT_SET)
                        .errorDescription("The version number was not passed and the SDK was not able to get it " +
                                "from the Android System")
                        .build();
            } else {
                return androidVersionNumber;
            }
        } else {
            return inputVersionNumber;
        }
    }


    private static String getAndroidAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static String getAndroidVersionNumber(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}