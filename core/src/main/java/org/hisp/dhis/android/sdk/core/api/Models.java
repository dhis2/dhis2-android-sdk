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

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.sdk.core.persistence.models.common.ModelsStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardItemContentStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardItemStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationCommentStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationStore;
import org.hisp.dhis.android.sdk.core.persistence.models.metadata.ConstantStore;
import org.hisp.dhis.android.sdk.core.persistence.models.user.UserAccountStore;
import org.hisp.dhis.android.sdk.core.persistence.models.user.UserStore;
import org.hisp.dhis.android.sdk.models.common.IModelsStore;
import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationElementStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationStore;
import org.hisp.dhis.android.sdk.models.metadata.Constant;
import org.hisp.dhis.android.sdk.models.user.IUserAccountStore;
import org.hisp.dhis.android.sdk.models.user.IUserStore;

public final class Models {
    private static Models models;

    // Meta data store objects
    private final IStore<Constant> constantStore;

    // Dashboard store objects
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // Interpretation store objects
    private final IInterpretationStore interpretationStore;
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IInterpretationElementStore interpretationElementStore;

    // User store object
    private final IUserAccountStore userAccountStore;
    private final IUserStore userStore;

    // Models store
    private final IModelsStore modelsStore;

    public Models(Context context) {
        FlowManager.init(context);

        constantStore = new ConstantStore();

        dashboardStore = new DashboardStore();
        dashboardItemStore = new DashboardItemStore();
        dashboardElementStore = new DashboardElementStore();
        dashboardItemContentStore = new DashboardItemContentStore();

        interpretationStore = new InterpretationStore();
        interpretationCommentStore = new InterpretationCommentStore();
        interpretationElementStore = new InterpretationElementStore();

        userAccountStore = new UserAccountStore();
        userStore = new UserStore();

        modelsStore = new ModelsStore();
    }

    public static void init(Context context) {
        models = new Models(context);
    }

    private static Models getInstance() {
        if (models == null) {
            throw new IllegalArgumentException("You should call inti method first");
        }

        return models;
    }

    public static IStore<Constant> constants() {
        return getInstance().constantStore;
    }

    public static IDashboardStore dashboards() {
        return getInstance().dashboardStore;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemStore;
    }

    public static IDashboardElementStore dashboardElements() {
        return getInstance().dashboardElementStore;
    }

    public static IDashboardItemContentStore dashboardItemContent() {
        return getInstance().dashboardItemContentStore;
    }

    public static IInterpretationStore interpretations() {
        return getInstance().interpretationStore;
    }

    public static IInterpretationCommentStore interpretationComments() {
        return getInstance().interpretationCommentStore;
    }

    public static IInterpretationElementStore interpretationElements() {
        return getInstance().interpretationElementStore;
    }

    public static IUserAccountStore userAccount() {
        return getInstance().userAccountStore;
    }

    public static IUserStore users() {
        return getInstance().userStore;
    }

    public static IModelsStore modelsStore() {
        return getInstance().modelsStore;
    }
}
