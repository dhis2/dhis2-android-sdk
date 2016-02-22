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

package org.hisp.dhis.client.sdk.core.common.persistence;

import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.core.common.IModelsStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.constant.IConstantStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardElementStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardStore;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementStore;
import org.hisp.dhis.client.sdk.core.dataset.IDataSetStore;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationElementStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionStore;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.*;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityInstanceStore;
import org.hisp.dhis.client.sdk.core.user.IUserAccountStore;
import org.hisp.dhis.client.sdk.core.user.IUserStore;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

public interface IPersistenceModule {
    ITransactionManager getTransactionManager();

    IStateStore getStateStore();

    // User store object
    IUserAccountStore getUserAccountStore();

    IModelsStore getModelStore();


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Dashboard stores.
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    IDashboardStore getDashboardStore();

    IDashboardItemStore getDashboardItemStore();

    IDashboardElementStore getDashboardElementStore();

    IDashboardItemContentStore getDashboardContentStore();

    IConstantStore getConstantStore();

    IDataElementStore getDataElementStore();

    IOptionStore getOptionStore();

    IOptionSetStore getOptionSetStore();

    IOrganisationUnitStore getOrganisationUnitStore();

    IProgramStore getProgramStore();

    IIdentifiableObjectStore<TrackedEntity> getTrackedEntityStore();

    ITrackedEntityAttributeStore getTrackedEntityAttributeStore();

    IProgramTrackedEntityAttributeStore getProgramTrackedEntityAttributeStore();

    IProgramStageDataElementStore getProgramStageDataElementStore();

    IProgramIndicatorStore getProgramIndicatorStore();

    IProgramStageSectionStore getProgramStageSectionStore();

    IProgramStageStore getProgramStageStore();

    IProgramRuleStore getProgramRuleStore();

    IProgramRuleActionStore getProgramRuleActionStore();

    IProgramRuleVariableStore getProgramRuleVariableStore();

    IIdentifiableObjectStore<RelationshipType> getRelationshipTypeStore();

    IDataSetStore getDataStore();

    //Tracker store objects
    ITrackedEntityAttributeValueStore getTrackedEntityAttributeValueStore();

    IRelationshipStore getRelationshipStore();

    ITrackedEntityInstanceStore getTrackedEntityInstanceStore();

    ITrackedEntityDataValueStore getTrackedEntityDataValueStore();

    IEventStore getEventStore();

    IEnrollmentStore getEnrollmentStore();

    // Interpretation store objects
    IIdentifiableObjectStore<Interpretation> getInterpretationStore();

    IInterpretationCommentStore getInterpretationCommentStore();

    IInterpretationElementStore getInterpretationElementStore();


    IUserStore getUserStore();

    IFailedItemStore getFailedItemStore();

}
