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

package org.hisp.dhis.client.sdk.core.interpretation;

import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;
import org.joda.time.DateTime;

public class InterpretationService implements IInterpretationService {
    private final InterpretationStore interpretationStore;
    private final StateStore stateStore;
    private final IInterpretationElementService interpretationElementService;

    public InterpretationService(InterpretationStore interpretationStore,
                                 StateStore stateStore, IInterpretationElementService
                                         interpretationElementService) {
        this.interpretationStore = interpretationStore;
        this.stateStore = stateStore;
        this.interpretationElementService = interpretationElementService;
    }

    /**
     * Creates interpretation from: chart, map, reportTable.
     * Please note, it won't work for data sets.
     * <p>
     * <p>
     * Note, model won't be saved to database automatically. You have to call .save()
     * both on interpretation and interpretation elements of current object.
     *
     * @param item DashboardItem which will represent content of interpretation.
     * @param user User who associated with Interpretation.
     * @param text Interpretation text written by user.
     * @return new Interpretation.
     */
    @Override
    public Interpretation create(DashboardItem item, User user, String text) {
        Preconditions.isNull(item, "DashboardItem object must not be null");
        Preconditions.isNull(user, "User object must not be null");
        Preconditions.isNull(text, "text must not be null");

        DateTime created = DateTime.now();
        Access access = Access.createDefaultAccess();

        Interpretation interpretation = new Interpretation();
        interpretation.setUId(CodeGenerator.generateCode());
        interpretation.setCreated(created);
        interpretation.setLastUpdated(created);
        interpretation.setName(text);
        interpretation.setDisplayName(text);
        interpretation.setAccess(access);
        interpretation.setText(text);
        interpretation.setUser(user);

        switch (item.getType()) {
            case Interpretation.TYPE_CHART: {
                InterpretationElement element = interpretationElementService
                        .create(interpretation, item.getChart(), Interpretation.TYPE_CHART);
                interpretation.setType(Interpretation.TYPE_CHART);
                interpretation.setChart(element);
                break;
            }
            case Interpretation.TYPE_MAP: {
                InterpretationElement element = interpretationElementService
                        .create(interpretation, item.getMap(), Interpretation.TYPE_MAP);
                interpretation.setType(Interpretation.TYPE_MAP);
                interpretation.setMap(element);
                break;
            }
            case Interpretation.TYPE_REPORT_TABLE: {
                InterpretationElement element = interpretationElementService
                        .create(interpretation, item.getReportTable(), Interpretation
                                .TYPE_REPORT_TABLE);
                interpretation.setType(Interpretation.TYPE_REPORT_TABLE);
                interpretation.setReportTable(element);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DashboardItem type");
            }
        }

        return interpretation;
    }

    @Override
    public boolean remove(Interpretation object) {
        Preconditions.isNull(object, "Interpretation object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            return false;
        }

        boolean status = false;
        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                status = stateStore.saveActionForModel(object, Action.TO_DELETE);
                break;
            }
            case TO_POST: {
                status = interpretationStore.delete(object);
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }
        }

        return status;
    }

    @Override
    public boolean save(Interpretation object) {
        Preconditions.isNull(object, "Dashboard object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = interpretationStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = interpretationStore.save(object);
                break;
            }
            case SYNCED: {
                status = interpretationStore.save(object);

                if (status) {
                    status = stateStore.saveActionForModel(object, Action.TO_UPDATE);
                }
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }

        }

        return status;
    }
}
