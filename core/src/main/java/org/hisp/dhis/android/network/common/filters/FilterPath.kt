/*
 *  Copyright (c) 2004-2025, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.network.common.filters

import kotlinx.serialization.KSerializer
import org.hisp.dhis.android.network.categorycombo.CategoryComboDTO
import org.hisp.dhis.android.network.eventfilter.EventFilterDTO
import org.hisp.dhis.android.network.externalmaplayer.ExternalMapLayerDTO
import org.hisp.dhis.android.network.option.OptionDTO
import org.hisp.dhis.android.network.optiongroup.OptionGroupDTO
import org.hisp.dhis.android.network.organisationunit.OrganisationUnitDTO
import org.hisp.dhis.android.network.programindicator.ProgramIndicatorDTO
import org.hisp.dhis.android.network.programrule.ProgramRuleDTO
import org.hisp.dhis.android.network.programstage.ProgramStageDTO
import org.hisp.dhis.android.network.programstageworkinglist.ProgramStageWorkingListDTO
import org.hisp.dhis.android.network.relationshiptype.RelationshipTypeDTO
import org.hisp.dhis.android.network.trackedentityinstancefilter.TrackedEntityInstanceFilterDTO

/**
 * Every API property path the SDK filters on, next to the DTO the filtered request returns. Being an enum, these are
 * the only paths a [Filter] can use, so `FilterPathShould` can check all of them against their DTO.
 */
internal enum class FilterPath(val dto: KSerializer<*>?, val path: String) {
    CATEGORY_COMBO_IS_DEFAULT(CategoryComboDTO.serializer(), "isDefault"),

    /** A server property the SDK never reads, so CategoryOptionDTO does not declare it and it cannot be checked. */
    CATEGORY_OPTION_CATEGORIES(dto = null, "categories.id"),
    EVENT_FILTER_PROGRAM(EventFilterDTO.serializer(), "program"),
    EXTERNAL_MAP_LAYER_POSITION(ExternalMapLayerDTO.serializer(), "mapLayerPosition"),
    OPTION_GROUP_OPTION_SET(OptionGroupDTO.serializer(), "optionSet.id"),
    OPTION_OPTION_SET(OptionDTO.serializer(), "optionSet.id"),
    ORGANISATION_UNIT_PATH(OrganisationUnitDTO.serializer(), "path"),
    PROGRAM_INDICATOR_DISPLAY_IN_FORM(ProgramIndicatorDTO.serializer(), "displayInForm"),
    PROGRAM_INDICATOR_PROGRAM(ProgramIndicatorDTO.serializer(), "program.id"),
    PROGRAM_RULE_PROGRAM(ProgramRuleDTO.serializer(), "program.id"),
    PROGRAM_STAGE_PROGRAM(ProgramStageDTO.serializer(), "program.id"),
    PROGRAM_STAGE_WORKING_LIST_PROGRAM(ProgramStageWorkingListDTO.serializer(), "program.id"),
    RELATIONSHIP_TYPE_LAST_UPDATED(RelationshipTypeDTO.serializer(), "lastUpdated"),
    TRACKED_ENTITY_INSTANCE_FILTER_PROGRAM(TrackedEntityInstanceFilterDTO.serializer(), "program.id"),
}
