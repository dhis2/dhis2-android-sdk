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

package org.hisp.dhis.android.sdk.core.api.api;

import android.content.Context;

import org.hisp.dhis.android.sdk.corejava.dashboard.DashboardContentService;
import org.hisp.dhis.android.sdk.corejava.dashboard.DashboardElementService;
import org.hisp.dhis.android.sdk.corejava.dashboard.DashboardItemService;
import org.hisp.dhis.android.sdk.corejava.dashboard.DashboardService;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardElementService;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemContentService;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardItemService;
import org.hisp.dhis.android.sdk.corejava.dashboard.IDashboardService;
import org.hisp.dhis.android.sdk.corejava.interpretation.IInterpretationCommentService;
import org.hisp.dhis.android.sdk.corejava.interpretation.IInterpretationElementService;
import org.hisp.dhis.android.sdk.corejava.interpretation.IInterpretationService;
import org.hisp.dhis.android.sdk.corejava.interpretation.InterpretationCommentService;
import org.hisp.dhis.android.sdk.corejava.interpretation.InterpretationElementService;
import org.hisp.dhis.android.sdk.corejava.interpretation.InterpretationService;
import org.hisp.dhis.android.sdk.core.api.modules.Models;
import org.hisp.dhis.android.sdk.core.common.state.StateStore;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.user.IUserAccountService;
import org.hisp.dhis.android.sdk.models.user.UserAccountService;

final class Services {
    private static Services services;

    private final IDashboardService dashboardService;
    private final IDashboardItemService dashboardItemService;
    private final IDashboardElementService dashboardElementService;
    private final IDashboardItemContentService dashboardItemContentService;

    private final IInterpretationService interpretationsService;
    private final IInterpretationElementService interpretationElementService;
    private final IInterpretationCommentService interpretationCommentService;

    private final IUserAccountService userAccountService;
    private final IStateStore stateStore;

    private Services(Context context) {
        Models.init(context);

        stateStore = new StateStore(null, null, null, null);

        dashboardItemService = new DashboardItemService(Models.dashboardItems(), Models.dashboardElements(), stateStore);
        dashboardElementService = new DashboardElementService(Models.dashboardElements(), dashboardItemService, stateStore);
        dashboardService = new DashboardService(Models.dashboards(), Models.dashboardItems(),
                Models.dashboardElements(), dashboardItemService, dashboardElementService, stateStore);
        dashboardItemContentService = new DashboardContentService(Models.dashboardItemContent());

        interpretationElementService = new InterpretationElementService();
        interpretationCommentService = new InterpretationCommentService(Models.interpretationComments(), stateStore);
        interpretationsService = new InterpretationService(Models.interpretations(), interpretationElementService);

        userAccountService = new UserAccountService(Models.userAccount(), Models.modelsStore());
    }

    public static void init(Context context) {
        if (services == null) {
            services = new Services(context);
        }
    }

    private static Services getInstance() {
        if (services == null) {
            throw new IllegalArgumentException("You have to call init() first");
        }

        return services;
    }

    public static IDashboardService dashboards() {
        return getInstance().dashboardService;
    }

    public static IDashboardItemService dashboardItems() {
        return getInstance().dashboardItemService;
    }

    public static IDashboardElementService dashboardElements() {
        return getInstance().dashboardElementService;
    }

    public static IDashboardItemContentService dashboardItemContents() {
        return getInstance().dashboardItemContentService;
    }

    public static IInterpretationService interpretations() {
        return getInstance().interpretationsService;
    }

    public static IInterpretationElementService interpretationElements() {
        return getInstance().interpretationElementService;
    }

    public static IInterpretationCommentService interpretationComments() {
        return getInstance().interpretationCommentService;
    }

    public static IUserAccountService userAccount() {
        return getInstance().userAccountService;
    }
}
