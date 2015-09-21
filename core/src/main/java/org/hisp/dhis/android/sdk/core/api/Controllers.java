/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.api;

import org.hisp.dhis.android.sdk.core.controllers.DashboardController;
import org.hisp.dhis.android.sdk.core.controllers.InterpretationController;
import org.hisp.dhis.android.sdk.core.controllers.common.IDataController;
import org.hisp.dhis.android.sdk.core.controllers.user.IUserAccountController;
import org.hisp.dhis.android.sdk.core.controllers.user.UserAccountController;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;

final class Controllers {
    private static Controllers controllers;

    private final IDataController<Dashboard> dashboardController;
    private final IDataController<Interpretation> interpretationController;
    private final IUserAccountController userAccountController;

    private Controllers(IDhisApi dhisApi) {
        dashboardController = new DashboardController(dhisApi, Models.dashboards(),
                Models.dashboardItems(), Models.dashboardElements(), Models.dashboardItemContent());
        interpretationController = new InterpretationController(dhisApi,
                Services.interpretations(), Services.userAccount());
        userAccountController = new UserAccountController(dhisApi, Models.userAccount());
    }

    public static void init(IDhisApi dhisApi) {
        if (controllers == null) {
            controllers = new Controllers(dhisApi);
        }
    }

    private static Controllers getInstance() {
        if (controllers == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return controllers;
    }

    public static IDataController<Dashboard> dashboards() {
        return getInstance().dashboardController;
    }

    public static IDataController<Interpretation> interpretations() {
        return getInstance().interpretationController;
    }

    public static IUserAccountController userAccount() {
        return getInstance().userAccountController;
    }
}
