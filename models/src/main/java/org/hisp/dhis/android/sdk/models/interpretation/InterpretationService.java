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

package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.user.User;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class InterpretationService implements IInterpretationService {
    private final IIdentifiableObjectStore<Interpretation> interpretationStore;
    private final IInterpretationElementService interpretationElementService;

    public InterpretationService(IIdentifiableObjectStore<Interpretation> interpretationStore,
                                 IInterpretationElementService interpretationElementService) {
        this.interpretationStore = interpretationStore;
        this.interpretationElementService = interpretationElementService;
    }

    /**
     * Creates comment for given interpretation. Comment is assigned to given user.
     *
     * @param interpretation Interpretation to associate comment with.
     * @param user           User who wants to create comment.
     * @param text           The actual content of comment.
     * @return Intrepretation comment.
     */
    @Override
    public InterpretationComment addComment(Interpretation interpretation, User user, String text) {
        isNull(interpretation, "interpretation must not be null");
        isNull(user, "user must not be null");

        DateTime lastUpdated = new DateTime();

        InterpretationComment comment = new InterpretationComment();
        comment.setCreated(lastUpdated);
        comment.setLastUpdated(lastUpdated);
        comment.setAccess(Access.createDefaultAccess());
        comment.setText(text);
        // comment.setAction(Action.TO_POST);
        comment.setUser(user);
        comment.setInterpretation(interpretation);
        return comment;
    }

    /**
     * Creates interpretation from: chart, map, reportTable.
     * Please note, it won't work for data sets.
     *
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
    public Interpretation add(DashboardItem item, User user, String text) {
        DateTime lastUpdated = new DateTime();

        Interpretation interpretation = new Interpretation();
        interpretation.setCreated(lastUpdated);
        interpretation.setLastUpdated(lastUpdated);
        interpretation.setAccess(Access.createDefaultAccess());
        interpretation.setText(text);
        // interpretation.setAction(Action.TO_POST);
        interpretation.setUser(user);

        switch (item.getType()) {
            case Interpretation.TYPE_CHART: {
                InterpretationElement element = interpretationElementService
                        .add(interpretation, item.getChart(), Interpretation.TYPE_CHART);
                interpretation.setType(Interpretation.TYPE_CHART);
                interpretation.setChart(element);
                break;
            }
            case Interpretation.TYPE_MAP: {
                InterpretationElement element = interpretationElementService
                        .add(interpretation, item.getMap(), Interpretation.TYPE_MAP);
                interpretation.setType(Interpretation.TYPE_MAP);
                interpretation.setMap(element);
                break;
            }
            case Interpretation.TYPE_REPORT_TABLE: {
                InterpretationElement element = interpretationElementService
                        .add(interpretation, item.getReportTable(), Interpretation.TYPE_REPORT_TABLE);
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
    public void update(Interpretation interpretation, String text) {
        interpretation.setText(text);

        /* if (interpretation.getAction() != Action.TO_DELETE &&
                interpretation.getAction() != Action.TO_POST) {
            interpretation.setAction(Action.TO_UPDATE);
        } */

        interpretationStore.save(interpretation);
    }

    @Override
    public void remove(Interpretation interpretation) {
        /* if (Action.TO_POST.equals(interpretation.getAction())) {
            interpretationStore.delete(interpretation);
        } else {
            interpretation.setAction(Action.TO_DELETE);
            interpretationStore.save(interpretation);
        } */
    }

    /**
     * Convenience method which allows to set InterpretationElements
     * to Interpretation depending on their mime-type.
     *
     * @param elements List of interpretation elements.
     */
    @Override
    public void setInterpretationElements(Interpretation interpretation, List<InterpretationElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        if (interpretation.getType() == null) {
            return;
        }

        if (interpretation.getType().equals(Interpretation.TYPE_DATA_SET_REPORT)) {
            for (InterpretationElement element : elements) {
                switch (element.getType()) {
                    case InterpretationElement.TYPE_DATA_SET: {
                        interpretation.setDataSet(element);
                        break;
                    }
                    case InterpretationElement.TYPE_PERIOD: {
                        interpretation.setPeriod(element);
                        break;
                    }
                    case InterpretationElement.TYPE_ORGANISATION_UNIT: {
                        interpretation.setOrganisationUnit(element);
                        break;
                    }
                }
            }
        } else {
            switch (interpretation.getType()) {
                case InterpretationElement.TYPE_CHART: {
                    interpretation.setChart(elements.get(0));
                    break;
                }
                case InterpretationElement.TYPE_MAP: {
                    interpretation.setMap(elements.get(0));
                    break;
                }
                case InterpretationElement.TYPE_REPORT_TABLE: {
                    interpretation.setReportTable(elements.get(0));
                    break;
                }
            }
        }
    }

    /**
     * Convenience method which allows to get
     * interpretation elements assigned to current object.
     *
     * @return List of interpretation elements.
     */
    @Override
    public List<InterpretationElement> getInterpretationElements(Interpretation interpretation) {
        List<InterpretationElement> elements = new ArrayList<>();

        switch (interpretation.getType()) {
            case Interpretation.TYPE_CHART: {
                elements.add(interpretation.getChart());
                break;
            }
            case Interpretation.TYPE_MAP: {
                elements.add(interpretation.getMap());
                break;
            }
            case Interpretation.TYPE_REPORT_TABLE: {
                elements.add(interpretation.getReportTable());
                break;
            }
            case Interpretation.TYPE_DATA_SET_REPORT: {
                elements.add(interpretation.getDataSet());
                elements.add(interpretation.getPeriod());
                elements.add(interpretation.getOrganisationUnit());
                break;
            }
        }

        return elements;
    }
}
