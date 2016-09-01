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

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementStore;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentStore;
import org.hisp.dhis.client.sdk.core.event.EventStore;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetStore;
import org.hisp.dhis.client.sdk.core.optionset.OptionStore;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.relationship.RelationshipStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.client.sdk.core.user.UserAccountStore;

public interface PersistenceModule {

    TransactionManager getTransactionManager();

    StateStore getStateStore();

    UserAccountStore getUserAccountStore();

    ProgramStore getProgramStore();

    ProgramStageStore getProgramStageStore();

    ProgramStageSectionStore getProgramStageSectionStore();

    ProgramStageDataElementStore getProgramStageDataElementStore();

    ProgramRuleStore getProgramRuleStore();

    ProgramRuleActionStore getProgramRuleActionStore();

    ProgramRuleVariableStore getProgramRuleVariableStore();

    ProgramIndicatorStore getProgramIndicatorStore();

    TrackedEntityAttributeStore getTrackedEntityAttributeStore();

    OrganisationUnitStore getOrganisationUnitStore();

    EventStore getEventStore();

    TrackedEntityStore getTrackedEntityStore();

    TrackedEntityDataValueStore getTrackedEntityDataValueStore();

    DataElementStore getDataElementStore();

    OptionSetStore getOptionSetStore();

    OptionStore getOptionStore();

    ProgramTrackedEntityAttributeStore getProgramTrackedEntityAttributeStore();

    EnrollmentStore getEnrollmentStore();

    TrackedEntityInstanceStore getTrackedEntityInstanceStore();

    RelationshipStore getRelationshipStore();

    TrackedEntityAttributeValueStore getTrackedEntityAttributeValueStore();

    boolean deleteAllTables();
}
